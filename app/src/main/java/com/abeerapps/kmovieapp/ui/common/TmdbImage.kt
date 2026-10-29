package com.abeerapps.kmovieapp.ui.common

import android.widget.ImageView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.R

/** TMDB image sizes, picked per use so small thumbnails don't download full-size images. */
enum class TmdbImageSize(val path: String) {
    PROFILE("w185"),
    POSTER("w342"),
    BACKDROP("w780"),
    POSTER_LARGE("w780"),
}

fun tmdbImageUrl(path: String?, size: TmdbImageSize): String? =
    path?.let { BuildConfig.TMDB_IMAGE_BASE_URL + size.path + it }

fun ImageView.loadTmdbImage(path: String?, size: TmdbImageSize) {
    load(tmdbImageUrl(path, size)) {
        crossfade(true)
        placeholder(R.drawable.bg_image_placeholder)
        error(R.drawable.bg_image_placeholder)
    }
}
