package com.abeerapps.kmovieapp.domain.repository

import androidx.paging.PagingData
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.Outcome
import kotlinx.coroutines.flow.Flow

/** Favorites of the signed-in TMDB account. Every call fails with UNAUTHORIZED when signed out. */
interface FavoritesRepository {
    /** Newest first. Emits fresh data whenever a favorite is added or removed. */
    fun getFavoriteMovies(): Flow<PagingData<Movie>>

    suspend fun isFavorite(movieId: Long): Outcome<Boolean>

    suspend fun setFavorite(movieId: Long, favorite: Boolean): Outcome<Unit>
}
