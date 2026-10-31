package com.abeerapps.kmovieapp.ui.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.usecase.GetMoviesByGenreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class GenreMoviesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMoviesByGenre: GetMoviesByGenreUseCase,
) : ViewModel() {

    private val genreId: Int = checkNotNull(savedStateHandle[ARG_GENRE_ID]) {
        "GenreMoviesFragment requires a genre id"
    }

    val movies: Flow<PagingData<Movie>> = getMoviesByGenre(genreId).cachedIn(viewModelScope)

    companion object {
        /** Fragment argument read through SavedStateHandle. */
        const val ARG_GENRE_ID = "genre_id"
    }
}
