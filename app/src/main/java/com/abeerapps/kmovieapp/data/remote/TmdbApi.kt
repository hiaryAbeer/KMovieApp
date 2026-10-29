package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.data.remote.dto.CreditsDto
import com.abeerapps.kmovieapp.data.remote.dto.GenresResponseDto
import com.abeerapps.kmovieapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmovieapp.data.remote.dto.MoviesPageDto
import com.abeerapps.kmovieapp.data.remote.dto.VideosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(): MoviesPageDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenresResponseDto

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
    ): MoviesPageDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Long): MovieDetailsDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Long): CreditsDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") movieId: Long): VideosResponseDto
}
