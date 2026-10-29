package com.abeerapps.kmovieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.abeerapps.kmovieapp.data.mapper.toBestTrailer
import com.abeerapps.kmovieapp.data.mapper.toDomain
import com.abeerapps.kmovieapp.data.paging.TmdbPagingSource
import com.abeerapps.kmovieapp.data.remote.TmdbApi
import com.abeerapps.kmovieapp.data.remote.safeApiCall
import com.abeerapps.kmovieapp.domain.model.CastMember
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Trailer
import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
) : MovieRepository {

    override suspend fun getTrendingMovies(): Outcome<List<Movie>> = safeApiCall {
        api.getTrendingMovies().results.map { it.toDomain() }
    }

    override suspend fun getGenres(): Outcome<List<Genre>> = safeApiCall {
        api.getMovieGenres().genres.map { it.toDomain() }
    }

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> =
        Pager(TmdbPagingSource.pagingConfig) {
            TmdbPagingSource { page -> api.discoverMovies(genreId, page) }
        }.flow

    override fun searchMovies(query: String): Flow<PagingData<Movie>> =
        Pager(TmdbPagingSource.pagingConfig) {
            TmdbPagingSource { page -> api.searchMovies(query, page) }
        }.flow

    override suspend fun getMovieDetails(movieId: Long): Outcome<MovieDetails> = safeApiCall {
        api.getMovieDetails(movieId).toDomain()
    }

    override suspend fun getMovieCast(movieId: Long): Outcome<List<CastMember>> = safeApiCall {
        api.getMovieCredits(movieId).cast
            .sortedBy { it.order }
            .take(MAX_CAST_MEMBERS)
            .map { it.toDomain() }
    }

    override suspend fun getMovieTrailer(movieId: Long): Outcome<Trailer?> = safeApiCall {
        api.getMovieVideos(movieId).results.toBestTrailer()
    }

    private companion object {
        const val MAX_CAST_MEMBERS = 20
    }
}
