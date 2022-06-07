package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.domain.BaseData
import com.abeerapps.kmovieapp.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBaseDataUseCase @Inject constructor(private val movieRepository: MovieRepository) {

     fun getBaseData(): Flow<BaseData> {
        return movieRepository.getBaseData()
    }
}