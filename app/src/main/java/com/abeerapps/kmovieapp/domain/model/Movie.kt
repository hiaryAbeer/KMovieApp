package com.abeerapps.kmovieapp.domain.model

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val voteCount: Int,
    val releaseDate: String?,
)
