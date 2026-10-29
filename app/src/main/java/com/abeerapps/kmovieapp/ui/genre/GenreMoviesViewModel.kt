package com.abeerapps.kmovieapp.ui.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.usecase.GetMoviesByGenreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreMoviesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMoviesByGenre: GetMoviesByGenreUseCase,
) : ViewModel() {

    private val genreId: Int = checkNotNull(savedStateHandle[GenreMoviesFragment.ARG_GENRE_ID]) {
        "GenreMoviesFragment requires a genre id"
    }

    private val _uiState = MutableStateFlow(GenreMoviesUiState())
    val uiState: StateFlow<GenreMoviesUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load(isRefresh = false)
    }

    fun retry() = load(isRefresh = false)

    fun refresh() = load(isRefresh = true)

    private fun load(isRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            }
            _uiState.update { state ->
                when (val result = getMoviesByGenre(genreId)) {
                    is Outcome.Success -> state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        movies = result.data,
                    )

                    is Outcome.Failure -> state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.error,
                    )
                }
            }
        }
    }
}
