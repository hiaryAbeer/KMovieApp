package com.abeerapps.kmovieapp.ui.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Collects [flow] while the fragment's *view* is at least STARTED.
 * Collection stops in the background and is not duplicated when the view is recreated.
 */
fun <T> Fragment.collectWithViewLifecycle(flow: Flow<T>, action: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(action)
        }
    }
}

/**
 * Like [collectWithViewLifecycle], but cancels [action] when a newer value arrives
 * (e.g. PagingData).
 */
fun <T> Fragment.collectLatestWithViewLifecycle(flow: Flow<T>, action: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(action)
        }
    }
}
