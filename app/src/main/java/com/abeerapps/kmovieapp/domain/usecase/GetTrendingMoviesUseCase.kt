package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetTrendingMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke() = repository.getTrendingMovies()
}
