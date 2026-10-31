package com.abeerapps.kmovieapp.domain.model

data class MovieDetails(
    val id: Long,
    val title: String,
    val tagline: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val voteCount: Int,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val genres: List<Genre>,
    val cast: List<CastMember> = emptyList(),
    val trailer: Trailer? = null,
)
