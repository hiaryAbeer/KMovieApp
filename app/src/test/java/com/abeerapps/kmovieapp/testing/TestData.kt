package com.abeerapps.kmovieapp.testing

import com.abeerapps.kmovieapp.domain.model.CastMember
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.model.MovieDetails
import com.abeerapps.kmovieapp.domain.model.Trailer

fun movie(id: Long = 1L, title: String = "Movie $id") = Movie(
    id = id,
    title = title,
    overview = "",
    posterPath = "/poster.jpg",
    backdropPath = "/backdrop.jpg",
    rating = 7.5,
    voteCount = 100,
    releaseDate = "2025-06-13",
)

fun genre(id: Int = 28, name: String = "Action") = Genre(id, name)

fun movieDetails(id: Long = 1L) = MovieDetails(
    id = id,
    title = "Movie $id",
    tagline = null,
    overview = "Overview",
    posterPath = "/poster.jpg",
    backdropPath = null,
    rating = 8.0,
    voteCount = 10,
    releaseDate = "2025-06-13",
    runtimeMinutes = 125,
    genres = listOf(genre()),
)

val sampleCast = listOf(CastMember(id = 7L, name = "Actor", character = "Hero", profilePath = null))
val sampleTrailer = Trailer("abc123")
