package com.abeerapps.kmovieapp.domain

import com.abeerapps.kmovieapp.domain.models.BaseData
import com.abeerapps.kmovieapp.domain.models.MovieDetailsModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getBaseData(): Flow<BaseData>

    fun getGenresData(): Flow<BaseData>

    fun getMoviesByGenresData(genres: Int): Flow<BaseData>

    fun getCast(movieId: Long): Flow<BaseData>

    fun getMovieDetail(movieId: Long): Flow<MovieDetailsModel>


}