package com.abeerapps.kmovieapp.domain.usecases

import com.abeerapps.kmovieapp.domain.MovieRepository
import javax.inject.Inject

class GetCastUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun getCast(movieId: Long) = movieRepository.getCast(movieId)
} 