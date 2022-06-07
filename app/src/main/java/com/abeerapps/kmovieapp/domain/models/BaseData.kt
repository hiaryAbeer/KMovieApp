package com.abeerapps.kmovieapp.domain.models

import com.google.gson.annotations.SerializedName

data class BaseData(
    @SerializedName("results")
    var list: List<MovieModel> = listOf(),
    @SerializedName("genres")
    var genresModelList: List<GenresModel> = listOf(),
    @SerializedName("cast")
    var castModels: List<CastModel> = listOf(),
)
