package com.abeerapps.kmovieapp.ui.common

import androidx.annotation.StringRes
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.domain.model.DataError

@StringRes
fun DataError.messageRes(): Int = when (this) {
    DataError.NETWORK -> R.string.error_network
    DataError.SERVER -> R.string.error_server
    DataError.UNAUTHORIZED -> R.string.error_unauthorized
    DataError.UNKNOWN -> R.string.error_unknown
}
