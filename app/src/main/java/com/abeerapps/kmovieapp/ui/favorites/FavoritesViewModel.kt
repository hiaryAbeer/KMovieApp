package com.abeerapps.kmovieapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.usecase.GetFavoriteMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class FavoritesViewModel @Inject constructor(getFavoriteMovies: GetFavoriteMoviesUseCase) :
    ViewModel() {

    val favorites: Flow<PagingData<Movie>> = getFavoriteMovies().cachedIn(viewModelScope)
}
