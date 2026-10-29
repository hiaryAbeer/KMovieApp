package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.data.remote.dto.AccountDto
import com.abeerapps.kmovieapp.data.remote.dto.AccountStatesDto
import com.abeerapps.kmovieapp.data.remote.dto.CreateSessionRequestDto
import com.abeerapps.kmovieapp.data.remote.dto.CreditsDto
import com.abeerapps.kmovieapp.data.remote.dto.DeleteSessionRequestDto
import com.abeerapps.kmovieapp.data.remote.dto.FavoriteRequestDto
import com.abeerapps.kmovieapp.data.remote.dto.GenresResponseDto
import com.abeerapps.kmovieapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmovieapp.data.remote.dto.MoviesPageDto
import com.abeerapps.kmovieapp.data.remote.dto.RequestTokenDto
import com.abeerapps.kmovieapp.data.remote.dto.SessionDto
import com.abeerapps.kmovieapp.data.remote.dto.StatusDto
import com.abeerapps.kmovieapp.data.remote.dto.ValidateWithLoginRequestDto
import com.abeerapps.kmovieapp.data.remote.dto.VideosResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
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

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
    ): MoviesPageDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Long): MovieDetailsDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Long): CreditsDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") movieId: Long): VideosResponseDto

    // region Authentication

    @GET("authentication/token/new")
    suspend fun createRequestToken(): RequestTokenDto

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLogin(@Body body: ValidateWithLoginRequestDto): RequestTokenDto

    @POST("authentication/session/new")
    suspend fun createSession(@Body body: CreateSessionRequestDto): SessionDto

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(@Body body: DeleteSessionRequestDto): StatusDto

    @GET("account")
    suspend fun getAccount(@Query("session_id") sessionId: String): AccountDto

    // endregion

    // region Favorites

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "created_at.desc",
    ): MoviesPageDto

    @POST("account/{account_id}/favorite")
    suspend fun setFavorite(
        @Path("account_id") accountId: Long,
        @Query("session_id") sessionId: String,
        @Body body: FavoriteRequestDto,
    ): StatusDto

    @GET("movie/{movie_id}/account_states")
    suspend fun getAccountStates(
        @Path("movie_id") movieId: Long,
        @Query("session_id") sessionId: String,
    ): AccountStatesDto

    // endregion
}
