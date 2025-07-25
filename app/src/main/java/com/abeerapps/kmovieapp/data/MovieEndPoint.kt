package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.domain.models.BaseData
import com.abeerapps.kmovieapp.domain.models.MovieDetailsModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val APIKey = "api_key=f0dd213b514dd22fa6d7790fdae32949"

interface MovieEndPoint {

    @GET("trending/all/day?$APIKey")
    suspend fun getBaseData(): BaseData

    @GET("genre/movie/list?$APIKey")
    suspend fun getGenresData(): BaseData

    @GET("discover/movie?$APIKey")
    suspend fun getMoviesByGenresData(@Query("with_genres") genres: Int, @Query("page") page: Int): BaseData

    @GET("movie/{movieId}?$APIKey")
    suspend fun getMovieDetail(@Path("movieId") movieId: Long): MovieDetailsModel

    @GET("movie/{movieId}/credits?$APIKey")
    suspend fun getCast(@Path("movieId") movieId: Long): BaseData
}