package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.domain.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun getMovieDetail(movieId: Long) = movieRepository.getMovieDetail(movieId)
}