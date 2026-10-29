package com.abeerapps.kmovieapp.ui.genre

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Movie

data class GenreMoviesUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: DataError? = null,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && movies.isEmpty()
}
