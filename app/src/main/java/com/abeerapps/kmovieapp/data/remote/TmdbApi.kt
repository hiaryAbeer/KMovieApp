package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.Account
import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.Authentication
import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.Movies
import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.PathParams
import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.QueryParams
import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.SortBy
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

/** TMDB v3 API. Paths and parameter names live in [TmdbEndpoints]. */
interface TmdbApi {

    @GET(Movies.TRENDING)
    suspend fun getTrendingMovies(): MoviesPageDto

    @GET(Movies.GENRES)
    suspend fun getMovieGenres(): GenresResponseDto

    @GET(Movies.DISCOVER)
    suspend fun discoverMovies(
        @Query(QueryParams.WITH_GENRES) genreId: Int,
        @Query(QueryParams.PAGE) page: Int,
    ): MoviesPageDto

    @GET(Movies.SEARCH)
    suspend fun searchMovies(
        @Query(QueryParams.QUERY) query: String,
        @Query(QueryParams.PAGE) page: Int,
    ): MoviesPageDto

    @GET(Movies.DETAILS)
    suspend fun getMovieDetails(@Path(PathParams.MOVIE_ID) movieId: Long): MovieDetailsDto

    @GET(Movies.CREDITS)
    suspend fun getMovieCredits(@Path(PathParams.MOVIE_ID) movieId: Long): CreditsDto

    @GET(Movies.VIDEOS)
    suspend fun getMovieVideos(@Path(PathParams.MOVIE_ID) movieId: Long): VideosResponseDto

    // region Authentication

    @GET(Authentication.NEW_REQUEST_TOKEN)
    suspend fun createRequestToken(): RequestTokenDto

    @POST(Authentication.VALIDATE_WITH_LOGIN)
    suspend fun validateWithLogin(@Body body: ValidateWithLoginRequestDto): RequestTokenDto

    @POST(Authentication.NEW_SESSION)
    suspend fun createSession(@Body body: CreateSessionRequestDto): SessionDto

    @HTTP(method = "DELETE", path = Authentication.SESSION, hasBody = true)
    suspend fun deleteSession(@Body body: DeleteSessionRequestDto): StatusDto

    @GET(Account.DETAILS)
    suspend fun getAccount(@Query(QueryParams.SESSION_ID) sessionId: String): AccountDto

    // endregion

    // region Favorites

    @GET(Account.FAVORITE_MOVIES)
    suspend fun getFavoriteMovies(
        @Path(PathParams.ACCOUNT_ID) accountId: Long,
        @Query(QueryParams.SESSION_ID) sessionId: String,
        @Query(QueryParams.PAGE) page: Int,
        @Query(QueryParams.SORT_BY) sortBy: String = SortBy.NEWEST_FIRST,
    ): MoviesPageDto

    @POST(Account.FAVORITE)
    suspend fun setFavorite(
        @Path(PathParams.ACCOUNT_ID) accountId: Long,
        @Query(QueryParams.SESSION_ID) sessionId: String,
        @Body body: FavoriteRequestDto,
    ): StatusDto

    @GET(Movies.ACCOUNT_STATES)
    suspend fun getAccountStates(
        @Path(PathParams.MOVIE_ID) movieId: Long,
        @Query(QueryParams.SESSION_ID) sessionId: String,
    ): AccountStatesDto

    // endregion
}
