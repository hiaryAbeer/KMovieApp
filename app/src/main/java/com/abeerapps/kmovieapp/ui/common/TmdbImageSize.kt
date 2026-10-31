package com.abeerapps.kmovieapp.ui.common

/** TMDB image sizes, picked per use so small thumbnails don't download full-size images. */
enum class TmdbImageSize(val path: String) {
    PROFILE("w185"),
    POSTER("w342"),
    BACKDROP("w780"),
    POSTER_LARGE("w780"),
}
