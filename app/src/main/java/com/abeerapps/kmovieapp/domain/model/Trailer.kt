package com.abeerapps.kmovieapp.domain.model

data class Trailer(val youtubeKey: String) {
    val url: String get() = "https://www.youtube.com/watch?v=$youtubeKey"
}
