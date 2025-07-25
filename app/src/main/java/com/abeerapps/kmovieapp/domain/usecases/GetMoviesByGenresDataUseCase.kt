package com.abeerapps.kmovieapp.domain.usecases

import com.abeerapps.kmovieapp.domain.MovieRepository
import com.abeerapps.kmovieapp.domain.models.GenresRequestModel
import javax.inject.Inject

class GetMoviesByGenresDataUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun getMoviesByGenresData(model: GenresRequestModel) = movieRepository.getMoviesByGenresData(model)
} 