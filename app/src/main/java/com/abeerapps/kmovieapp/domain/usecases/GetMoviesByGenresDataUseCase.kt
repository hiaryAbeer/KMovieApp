package com.abeerapps.kmovieapp.domain.usecases

import com.abeerapps.kmovieapp.domain.MovieRepository
import javax.inject.Inject

class GetMoviesByGenresDataUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun getMoviesByGenresData(genres: Int) = movieRepository.getMoviesByGenresData(genres)
} 