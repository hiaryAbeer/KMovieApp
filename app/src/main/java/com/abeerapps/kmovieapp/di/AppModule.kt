package com.abeerapps.kmovieapp.di

import com.abeerapps.kmovieapp.data.MovieEndPoint
import com.abeerapps.kmovieapp.data.MovieRepositoryImpl
import com.abeerapps.kmovieapp.domain.MovieRepository
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideOkHttpProfiler(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(OkHttpProfilerInterceptor())
        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit): MovieEndPoint {
        return retrofit.create(MovieEndPoint::class.java)
    }

    @Provides
    fun provideMovieRepository(movieEndPoint: MovieEndPoint): MovieRepository =
        MovieRepositoryImpl(movieEndPoint)

}