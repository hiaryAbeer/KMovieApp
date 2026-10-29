package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesByGenreUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(genreId: Int) = repository.getMoviesByGenre(genreId)
}
