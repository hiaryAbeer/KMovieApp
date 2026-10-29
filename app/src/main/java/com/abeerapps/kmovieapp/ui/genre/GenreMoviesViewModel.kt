package com.abeerapps.kmovieapp.ui.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.usecase.GetMoviesByGenreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GenreMoviesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMoviesByGenre: GetMoviesByGenreUseCase,
) : ViewModel() {

    private val genreId: Int = checkNotNull(savedStateHandle[GenreMoviesFragment.ARG_GENRE_ID]) {
        "GenreMoviesFragment requires a genre id"
    }

    val movies: Flow<PagingData<Movie>> = getMoviesByGenre(genreId).cachedIn(viewModelScope)
}
