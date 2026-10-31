package com.abeerapps.kmovieapp.ui.home

import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Genre
import com.abeerapps.kmovieapp.domain.model.Movie

data class HomeUiState(
    val isLoading: Boolean = true,
    val trending: List<Movie> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val error: DataError? = null,
)
