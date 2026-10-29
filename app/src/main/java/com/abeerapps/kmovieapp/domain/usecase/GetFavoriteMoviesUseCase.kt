package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke() = favoritesRepository.getFavoriteMovies()
}
