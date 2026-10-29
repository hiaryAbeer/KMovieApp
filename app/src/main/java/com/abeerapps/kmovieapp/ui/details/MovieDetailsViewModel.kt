package com.abeerapps.kmovieapp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.repository.AuthRepository
import com.abeerapps.kmovieapp.domain.repository.FavoritesRepository
import com.abeerapps.kmovieapp.domain.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetails: GetMovieDetailsUseCase,
    private val favoritesRepository: FavoritesRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    private val movieId = MovieDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle).movieId

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private val _favoriteState = MutableStateFlow(FavoriteUiState())
    val favoriteState: StateFlow<FavoriteUiState> = _favoriteState.asStateFlow()

    private val _events = Channel<MovieDetailsEvent>(Channel.BUFFERED)
    val events: Flow<MovieDetailsEvent> = _events.receiveAsFlow()

    private var loadJob: Job? = null

    /** Set when the heart was tapped while signed out, so it applies once sign-in completes. */
    private var favoriteAfterSignIn = false

    init {
        load()
        viewModelScope.launch {
            authRepository.session.collectLatest { session ->
                if (session == null) {
                    _favoriteState.value = FavoriteUiState()
                } else {
                    refreshFavoriteStatus()
                }
            }
        }
    }

    fun retry() = load()

    fun onFavoriteClick() {
        val current = _favoriteState.value
        if (current.isUpdating) return
        if (!current.isSignedIn) {
            favoriteAfterSignIn = true
            _events.trySend(MovieDetailsEvent.RequireSignIn)
            return
        }
        setFavorite(!current.isFavorite)
    }

    private suspend fun refreshFavoriteStatus() {
        _favoriteState.value = FavoriteUiState(isSignedIn = true, isUpdating = true)
        val isFavorite = (favoritesRepository.isFavorite(movieId) as? Outcome.Success)?.data ?: false
        _favoriteState.value = FavoriteUiState(isSignedIn = true, isFavorite = isFavorite)

        if (favoriteAfterSignIn) {
            favoriteAfterSignIn = false
            if (!isFavorite) setFavorite(true)
        }
    }

    /** Updates the heart immediately and rolls back if TMDB rejects the change. */
    private fun setFavorite(favorite: Boolean) {
        _favoriteState.update { it.copy(isFavorite = favorite, isUpdating = true) }
        viewModelScope.launch {
            when (val result = favoritesRepository.setFavorite(movieId, favorite)) {
                is Outcome.Success -> _favoriteState.update { it.copy(isUpdating = false) }
                is Outcome.Failure -> {
                    _favoriteState.update { it.copy(isFavorite = !favorite, isUpdating = false) }
                    // The repository clears an expired session, so ask the user to sign in again.
                    val event = if (result.error == DataError.UNAUTHORIZED) {
                        MovieDetailsEvent.RequireSignIn
                    } else {
                        MovieDetailsEvent.ShowError(result.error)
                    }
                    _events.send(event)
                }
            }
        }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            _uiState.value = when (val result = getMovieDetails(movieId)) {
                is Outcome.Success -> MovieDetailsUiState.Content(result.data)
                is Outcome.Failure -> MovieDetailsUiState.Error(result.error)
            }
        }
    }
}
