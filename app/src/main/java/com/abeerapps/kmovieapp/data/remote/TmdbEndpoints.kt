package com.abeerapps.kmovieapp.data.remote

import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.PathParams.ACCOUNT_ID
import com.abeerapps.kmovieapp.data.remote.TmdbEndpoints.PathParams.MOVIE_ID

/**
 * TMDB v3 endpoint paths and parameter names, relative to `BuildConfig.TMDB_BASE_URL`.
 *
 * Paths reuse the [PathParams] constants for their `{placeholders}`, so a path and its
 * `@Path` parameter can't drift apart. Everything is `const` so it can be used in annotations.
 */
internal object TmdbEndpoints {

    object Movies {
        const val TRENDING = "trending/movie/day"
        const val GENRES = "genre/movie/list"
        const val DISCOVER = "discover/movie"
        const val SEARCH = "search/movie"
        const val DETAILS = "movie/{$MOVIE_ID}"
        const val CREDITS = "movie/{$MOVIE_ID}/credits"
        const val VIDEOS = "movie/{$MOVIE_ID}/videos"
        const val ACCOUNT_STATES = "movie/{$MOVIE_ID}/account_states"
    }

    object Authentication {
        const val NEW_REQUEST_TOKEN = "authentication/token/new"
        const val VALIDATE_WITH_LOGIN = "authentication/token/validate_with_login"
        const val NEW_SESSION = "authentication/session/new"
        const val SESSION = "authentication/session"
    }

    object Account {
        const val DETAILS = "account"
        const val FAVORITE_MOVIES = "account/{$ACCOUNT_ID}/favorite/movies"
        const val FAVORITE = "account/{$ACCOUNT_ID}/favorite"
    }

    object PathParams {
        const val MOVIE_ID = "movie_id"
        const val ACCOUNT_ID = "account_id"
    }

    object QueryParams {
        const val API_KEY = "api_key"
        const val SESSION_ID = "session_id"
        const val PAGE = "page"
        const val QUERY = "query"
        const val WITH_GENRES = "with_genres"
        const val SORT_BY = "sort_by"
    }

    object SortBy {
        const val NEWEST_FIRST = "created_at.desc"
    }
}
