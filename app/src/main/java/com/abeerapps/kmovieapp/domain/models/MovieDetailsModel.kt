package com.abeerapps.kmovieapp.domain.models

import com.abeerapps.kmovieapp.domain.models.GenresModel
import com.google.gson.annotations.SerializedName

data class MovieDetailsModel(

    @SerializedName("original_title")
    var name: String? = null,

    @SerializedName("poster_path")
    val movieImage: String? = null,

    @SerializedName("popularity")
    val popularity: Double = 0.0,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("vote_average")
    val vote_average: Double = 0.0,

    @SerializedName("genres")
    var genresModelList: MutableList<GenresModel?>? = null,

    @SerializedName("video")
    var video : Boolean = false
)
