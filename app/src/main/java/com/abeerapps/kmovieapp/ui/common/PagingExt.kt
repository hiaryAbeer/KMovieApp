package com.abeerapps.kmovieapp.ui.common

import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.DataErrorException
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * Shows a message when a refresh fails while the list still has items (e.g. pull-to-refresh
 * offline). The full-screen error only covers empty lists, so without this the failure is silent.
 */
fun Fragment.showRefreshErrors(adapter: PagingDataAdapter<*, *>) {
    val refreshErrors = adapter.loadStateFlow
        .map { loadStates ->
            (loadStates.refresh as? LoadState.Error)?.error?.takeIf { adapter.itemCount > 0 }
        }
        .distinctUntilChanged()
        .filterNotNull()
    collectWithViewLifecycle(refreshErrors) { error ->
        Snackbar.make(requireView(), error.asDataError().messageRes(), Snackbar.LENGTH_LONG).show()
    }
}

fun Throwable.asDataError(): DataError = (this as? DataErrorException)?.error ?: DataError.UNKNOWN

/** Two-column grid with a full-width loading/retry footer. */
fun RecyclerView.setUpPagedMovieGrid(adapter: MovieGridAdapter) {
    val footer = MovieLoadStateAdapter(adapter::retry)
    val concatAdapter: ConcatAdapter = adapter.withLoadStateFooter(footer)
    layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (position >= adapter.itemCount) GRID_SPAN_COUNT else 1
        }
    }
    this.adapter = concatAdapter
}

private const val GRID_SPAN_COUNT = 2
