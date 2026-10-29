package com.abeerapps.kmovieapp.domain.repository

import com.abeerapps.kmovieapp.domain.model.CastMember
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Trailer

interface MovieRepository {
    suspend fun getTrendingMovies(): Outcome<List<Movie>>

    suspend fun getGenres(): Outcome<List<Genre>>

    suspend fun getMoviesByGenre(genreId: Int, page: Int): Outcome<List<Movie>>

    suspend fun getMovieDetails(movieId: Long): Outcome<MovieDetails>

    suspend fun getMovieCast(movieId: Long): Outcome<List<CastMember>>

    suspend fun getMovieTrailer(movieId: Long): Outcome<Trailer?>
}
