package com.abeerapps.kmovieapp.domain

import com.google.gson.annotations.SerializedName

data class MovieModel(
     var id: Long = 0,
    @SerializedName("original_language") val originalLanguage: String? = null,

    @SerializedName("original_title")
    val originalTitle: String? = null,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("video")
    val video: Boolean = false,

    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,

    @SerializedName("vote_count")
    val voteCount: Int = 0,

    @SerializedName("popularity")
    val popularity: Double = 0.0,

    @SerializedName("media_type")
    val mediaType: String? = null,
)