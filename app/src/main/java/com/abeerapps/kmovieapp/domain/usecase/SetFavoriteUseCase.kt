package com.abeerapps.kmovieapp.domain.usecase

import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import javax.inject.Inject

class SetFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(movieId: Long, favorite: Boolean) =
        favoritesRepository.setFavorite(movieId, favorite)
}
