package com.abeerapps.kmovieapp.ui.common

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.abeerapps.kmovieapp.domain.model.DataError

/** What a paged screen should show while its list is empty. */
data class PagedListState(val isLoading: Boolean, val error: DataError?, val isEmpty: Boolean)

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
