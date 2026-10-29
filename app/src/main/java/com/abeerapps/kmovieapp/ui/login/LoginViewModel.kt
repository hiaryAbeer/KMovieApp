package com.abeerapps.kmovieapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.usecase.SignInWithCredentialsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Username/password sign-in. The password is held in memory only: it is deliberately not put
 * in SavedStateHandle (which can be written to disk) and is cleared once sign-in succeeds.
 * Leaving the screen is driven by the session, so it works for both sign-in methods.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithCredentials: SignInWithCredentialsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _signedIn = Channel<Session>(Channel.BUFFERED)

    /** One-off: emits the new session after a successful credentials sign-in. */
    val signedIn: Flow<Session> = _signedIn.receiveAsFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmit() {
        val current = _uiState.value
        if (!current.canSubmit) return
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val result = signInWithCredentials(current.username, current.password)
            _uiState.update { state ->
                when (result) {
                    is Outcome.Success -> state.copy(password = "", isSubmitting = false)
                    is Outcome.Failure -> state.copy(isSubmitting = false, error = result.error)
                }
            }
            if (result is Outcome.Success) _signedIn.send(result.data)
        }
    }
}
