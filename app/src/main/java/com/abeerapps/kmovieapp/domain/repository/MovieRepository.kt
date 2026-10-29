package com.abeerapps.kmovieapp.domain.repository

import androidx.paging.PagingData
import com.abeerapps.kmovieapp.domain.model.CastMember
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Trailer
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getTrendingMovies(): Outcome<List<Movie>>

    suspend fun getGenres(): Outcome<List<Genre>>

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>

    fun searchMovies(query: String): Flow<PagingData<Movie>>

    suspend fun getMovieDetails(movieId: Long): Outcome<MovieDetails>

    suspend fun getMovieCast(movieId: Long): Outcome<List<CastMember>>

    suspend fun getMovieTrailer(movieId: Long): Outcome<Trailer?>
}
