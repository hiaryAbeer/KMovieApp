package com.abeerapps.kmovieapp.ui.details

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.MovieDetails

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState
    data class Content(val details: MovieDetails) : MovieDetailsUiState
    data class Error(val error: DataError) : MovieDetailsUiState
}

data class FavoriteUiState(
    val isSignedIn: Boolean = false,
    val isFavorite: Boolean = false,
    val isUpdating: Boolean = false,
)

sealed interface MovieDetailsEvent {
    data object RequireSignIn : MovieDetailsEvent
    data class ShowError(val error: DataError) : MovieDetailsEvent
}
