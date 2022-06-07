package com.abeerapps.kmovieapp.domain.models

import com.google.gson.annotations.SerializedName

data class CastModel(
    @SerializedName("cast_id")
     var cast_id: Int = 0,

    @SerializedName("character")
     val character: String? = null,

    @SerializedName("credit_id")
     val credit_id: String? = null,

    @SerializedName("gender")
     val gender: Int = 0,

    @SerializedName("id")
     val id: Int = 0,

    @SerializedName("name")
     val name: String? = null,

    @SerializedName("order")
     val order: Int = 0,

    @SerializedName("profile_path")
     var profile_path: String? = null,
)
