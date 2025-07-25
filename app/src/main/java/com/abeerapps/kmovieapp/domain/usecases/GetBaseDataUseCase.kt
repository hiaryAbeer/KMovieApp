package com.abeerapps.kmovieapp.domain.usecases

import com.abeerapps.kmovieapp.domain.models.BaseData
import com.abeerapps.kmovieapp.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBaseDataUseCase @Inject constructor(private val movieRepository: MovieRepository) {

     fun getBaseData(): Flow<BaseData> {
        return movieRepository.getBaseData()
    }
} 