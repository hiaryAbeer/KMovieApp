package com.abeerapps.kmovieapp.domain

import com.google.gson.annotations.SerializedName

data class GenresModel(
    @SerializedName("id")
     var id: Int = 0,

    @SerializedName("name")
     var name: String = "",
)
