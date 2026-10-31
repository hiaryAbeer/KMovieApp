package com.abeerapps.kmovieapp.ui.login

import com.abeerapps.kmovieapp.domain.model.DataError

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: DataError? = null,
) {
    val canSubmit: Boolean get() = username.isNotBlank() && password.isNotEmpty() && !isSubmitting
}
