package com.abeerapps.kmovieapp.domain.models

import com.google.gson.annotations.SerializedName

data class GenresRequestModel(
    @SerializedName("genres")
    var genres: Int = -1,
    @SerializedName("page")
     var page: Int = 1,
)
