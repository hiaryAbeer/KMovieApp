package com.abeerapps.kmovieapp.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abeerapps.kmovieapp.domain.model.Movie
import com.abeerapps.kmovieapp.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    searchMovies: SearchMoviesUseCase,
) : ViewModel() {

    /** Survives process death so the user comes back to the same results. */
    val query: StateFlow<String> = savedStateHandle.getStateFlow(KEY_QUERY, "")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: Flow<PagingData<Movie>> = query
        .map { it.trim() }
        // Clearing the field resets results immediately; typing waits for a pause.
        .debounce { if (it.isEmpty()) 0L else DEBOUNCE_MILLIS }
        .distinctUntilChanged()
        .flatMapLatest { if (it.length < MIN_QUERY_LENGTH) flowOf(PagingData.empty()) else searchMovies(it) }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        savedStateHandle[KEY_QUERY] = query
    }

    companion object {
        private const val KEY_QUERY = "query"
        const val MIN_QUERY_LENGTH = 2
        private const val DEBOUNCE_MILLIS = 400L
    }
}
