package com.abeerapps.kmovieapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    favoritesRepository: FavoritesRepository,
) : ViewModel() {

    val favorites: Flow<PagingData<Movie>> =
        favoritesRepository.getFavoriteMovies().cachedIn(viewModelScope)
}
