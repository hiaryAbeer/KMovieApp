package com.abeerapps.kmovieapp.domain.usecases

import com.abeerapps.kmovieapp.domain.MovieRepository
import javax.inject.Inject

class GetGenresDataUseCase @Inject constructor(private val movieRepository: MovieRepository) {

    fun getGenresData() = movieRepository.getGenresData()
} 