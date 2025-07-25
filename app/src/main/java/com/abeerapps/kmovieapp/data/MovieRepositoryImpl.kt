package com.abeerapps.kmovieapp.data

import android.util.Log
import com.abeerapps.kmovieapp.domain.MovieRepository
import com.abeerapps.kmovieapp.domain.models.BaseData
import com.abeerapps.kmovieapp.domain.models.GenresRequestModel
import com.abeerapps.kmovieapp.domain.models.MovieDetailsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val movieEndPoint: MovieEndPoint) :
    MovieRepository {
    override fun getBaseData(): Flow<BaseData> = flow {
        try {
            val response: BaseData = movieEndPoint.getBaseData()
            emit(response)
        } catch (e: Exception) {

        }
    }

    override fun getGenresData(): Flow<BaseData> = flow {
        try {
            val response = movieEndPoint.getGenresData()
            Log.d("getGenresData", response.genresModelList?.size.toString())
            emit(response)
        } catch (e: Exception) {
            Log.d("getGenresData", e.toString())

        }
    }

    override fun getMoviesByGenresData(model: GenresRequestModel): Flow<BaseData> = flow {
        try {
            val response: BaseData = movieEndPoint.getMoviesByGenresData(model.genres, model.page)
            emit(response)

        } catch (e: Exception) {

        }
    }

    override fun getCast(movieId: Long): Flow<BaseData> = flow {
        try {
            val response: BaseData = movieEndPoint.getCast(movieId)
            emit(response)

        } catch (e: Exception) {

        }
    }

    override fun getMovieDetail(movieId: Long): Flow<MovieDetailsModel> = flow {
        try {
            val response: MovieDetailsModel = movieEndPoint.getMovieDetail(movieId)
            emit(response)

        } catch (e: Exception) {

        }
    }
}