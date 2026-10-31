package com.abeerapps.kmovieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.usecase.GetGenresUseCase
import com.abeerapps.kmovieapp.domain.usecase.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingMovies: GetTrendingMoviesUseCase,
    private val getGenres: GetGenresUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val trending = async { getTrendingMovies() }
            val genres = async { getGenres() }

            _uiState.update { state ->
                when (val genresResult = genres.await()) {
                    // Genres drive the tabs, so without them there is no usable screen.
                    is Outcome.Failure -> state.copy(isLoading = false, error = genresResult.error)
                    is Outcome.Success -> state.copy(
                        isLoading = false,
                        genres = genresResult.data,
                        trending = (trending.await() as? Outcome.Success)?.data ?: state.trending,
                    )
                }
            }
        }
    }
}
