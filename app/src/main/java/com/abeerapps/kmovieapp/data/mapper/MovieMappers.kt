package com.abeerapps.kmovieapp.data.mapper

import com.abeerapps.kmovieapp.data.remote.dto.CastDto
import com.abeerapps.kmovieapp.data.remote.dto.GenreDto
import com.abeerapps.kmovieapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmovieapp.data.remote.dto.MovieDto
import com.abeerapps.kmovieapp.data.remote.dto.VideoDto
import com.abeerapps.kmovieapp.domain.model.CastMember
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.domain.model.Trailer

fun MovieDto.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate?.takeIf { it.isNotBlank() },
)

fun GenreDto.toDomain() = Genre(id = id, name = name)

fun MovieDetailsDto.toDomain() = MovieDetails(
    id = id,
    title = title,
    tagline = tagline?.takeIf { it.isNotBlank() },
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate?.takeIf { it.isNotBlank() },
    runtimeMinutes = runtime?.takeIf { it > 0 },
    genres = genres.map { it.toDomain() },
)

fun CastDto.toDomain() = CastMember(
    id = id,
    name = name,
    character = character.orEmpty(),
    profilePath = profilePath,
)

/** Picks the best YouTube trailer: official trailers first, then any trailer, then a teaser. */
fun List<VideoDto>.toBestTrailer(): Trailer? =
    filter { it.site.equals("YouTube", ignoreCase = true) }
        .sortedWith(
            compareByDescending<VideoDto> { it.type == "Trailer" }
                .thenByDescending { it.official }
                .thenByDescending { it.type == "Teaser" },
        )
        .firstOrNull { it.type == "Trailer" || it.type == "Teaser" }
        ?.let { Trailer(it.key) }
