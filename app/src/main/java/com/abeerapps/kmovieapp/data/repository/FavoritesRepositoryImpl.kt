package com.abeerapps.kmovieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.abeerapps.kmovieapp.data.local.SessionStore
import com.abeerapps.kmovieapp.data.paging.TmdbPagingSource
import com.abeerapps.kmovieapp.data.remote.TmdbApi
import com.abeerapps.kmovieapp.data.remote.dto.FavoriteRequestDto
import com.abeerapps.kmovieapp.data.remote.safeApiCall
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    private val sessionStore: SessionStore,
) : FavoritesRepository {

    /** Bumped on every change so open favorites lists reload. */
    private val changes = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFavoriteMovies(): Flow<PagingData<Movie>> =
        combine(sessionStore.session, changes) { session, _ -> session }
            .flatMapLatest { session ->
                if (session == null) {
                    flowOf(PagingData.empty())
                } else {
                    Pager(TmdbPagingSource.pagingConfig) {
                        TmdbPagingSource { page ->
                            api.getFavoriteMovies(session.accountId, session.sessionId, page)
                        }
                    }.flow
                }
            }

    override suspend fun isFavorite(movieId: Long): Outcome<Boolean> = withSession { session ->
        api.getAccountStates(movieId, session.sessionId).favorite
    }

    override suspend fun setFavorite(movieId: Long, favorite: Boolean): Outcome<Unit> =
        withSession { session ->
            api.setFavorite(
                accountId = session.accountId,
                sessionId = session.sessionId,
                body = FavoriteRequestDto(mediaType = "movie", mediaId = movieId, favorite = favorite),
            )
            changes.value++
        }

    /** Runs [block] with the current session; a rejected session is cleared so the UI signs out. */
    private suspend fun <T> withSession(block: suspend (Session) -> T): Outcome<T> {
        val session = sessionStore.current() ?: return Outcome.Failure(DataError.UNAUTHORIZED)
        val result = safeApiCall { block(session) }
        if (result is Outcome.Failure && result.error == DataError.UNAUTHORIZED) sessionStore.clear()
        return result
    }
}
