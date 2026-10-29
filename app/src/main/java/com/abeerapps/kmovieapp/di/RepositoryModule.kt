package com.abeerapps.kmovieapp.di

import com.abeerapps.kmovieapp.data.repository.AuthRepositoryImpl
import com.abeerapps.kmovieapp.data.repository.FavoritesRepositoryImpl
import com.abeerapps.kmovieapp.data.repository.MovieRepositoryImpl
import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import com.abeerapps.kmovieapp.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
}
