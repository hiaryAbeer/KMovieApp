package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.domain.BaseData
import com.abeerapps.kmovieapp.domain.MovieDetailsModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieEndPoint {

    @GET("trending/all/day?api_key=f0dd213b514dd22fa6d7790fdae32949")
    suspend fun getBaseData(): BaseData

    @GET("genre/movie/list?api_key=f0dd213b514dd22fa6d7790fdae32949")
    suspend fun getGenresData(): BaseData

    @GET("discover/movie?api_key=f0dd213b514dd22fa6d7790fdae32949&page=1")
    suspend fun getMoviesByGenresData(@Query("with_genres") genres: Int): BaseData

    @GET("movie/{movieId}?api_key=f0dd213b514dd22fa6d7790fdae32949")
    suspend fun getMovieDetail(@Path("movieId") movieId: Long): MovieDetailsModel

    @GET("movie/{movieId}/credits?api_key=f0dd213b514dd22fa6d7790fdae32949")
    suspend fun getCast(@Path("movieId") movieId: Long): BaseData
}