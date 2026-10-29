package com.abeerapps.kmovieapp.domain.model

import java.net.URLEncoder

data class Trailer(val youtubeKey: String) {
    /** The key comes from the API, so it is encoded rather than trusted to be URL-safe. */
    val url: String
        get() = YOUTUBE_WATCH_URL + URLEncoder.encode(youtubeKey, Charsets.UTF_8.name())

    private companion object {
        const val YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v="
    }
}
