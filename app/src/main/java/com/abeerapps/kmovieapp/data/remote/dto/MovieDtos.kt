package com.abeerapps.kmovieapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesPageDto(
    val page: Int = 1,
    val results: List<MovieDto> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 1,
)

@Serializable
data class MovieDto(
    val id: Long,
    val title: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("release_date") val releaseDate: String? = null,
)

@Serializable
data class MovieDetailsDto(
    val id: Long,
    val title: String = "",
    val tagline: String? = null,
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    val genres: List<GenreDto> = emptyList(),
)

@Serializable
data class GenresResponseDto(
    val genres: List<GenreDto> = emptyList(),
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String = "",
)

@Serializable
data class CreditsDto(
    val cast: List<CastDto> = emptyList(),
)

@Serializable
data class CastDto(
    val id: Long,
    val name: String = "",
    val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
    val order: Int = Int.MAX_VALUE,
)

@Serializable
data class VideosResponseDto(
    val results: List<VideoDto> = emptyList(),
)

@Serializable
data class VideoDto(
    val key: String,
    val site: String = "",
    val type: String = "",
    val official: Boolean = false,
)
