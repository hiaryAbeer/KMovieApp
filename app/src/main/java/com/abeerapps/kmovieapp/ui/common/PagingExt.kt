package com.abeerapps.kmovieapp.ui.common

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.DataErrorException

/** What a paged screen should show while its list is empty. */
data class PagedListState(
    val isLoading: Boolean,
    val error: DataError?,
    val isEmpty: Boolean,
)

/**
 * Full-screen states only apply while there is nothing to show: once items are loaded,
 * refresh and append problems are reported by the pull-to-refresh spinner and the list footer.
 */
fun CombinedLoadStates.toPagedListState(itemCount: Int): PagedListState {
    val hasItems = itemCount > 0
    return PagedListState(
        isLoading = refresh is LoadState.Loading && !hasItems,
        error = (refresh as? LoadState.Error)?.error?.asDataError()?.takeUnless { hasItems },
        isEmpty = refresh is LoadState.NotLoading && !hasItems,
    )
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
