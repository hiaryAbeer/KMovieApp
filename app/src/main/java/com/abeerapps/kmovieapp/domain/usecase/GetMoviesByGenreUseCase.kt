package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesByGenreUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(genreId: Int, page: Int = 1) =
        repository.getMoviesByGenre(genreId, page)
}
