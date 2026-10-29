package com.abeerapps.kmovieapp.ui.common

import androidx.core.view.isVisible
import com.abeerapps.kmovieapp.databinding.ViewErrorBinding
import com.abeerapps.kmovieapp.domain.model.DataError

/** Shows the full-screen error for [error], or hides it when [error] is null. */
fun ViewErrorBinding.render(error: DataError?) {
    root.isVisible = error != null
    error?.let { errorMessage.setText(it.messageRes()) }
}

fun ViewErrorBinding.setOnRetryClickListener(onRetry: () -> Unit) {
    retryButton.setOnClickListener { onRetry() }
}
