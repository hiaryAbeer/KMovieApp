package com.abeerapps.kmovieapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.usecase.CompleteSignInUseCase
import com.abeerapps.kmovieapp.domain.usecase.ObserveSessionUseCase
import com.abeerapps.kmovieapp.domain.usecase.SignOutUseCase
import com.abeerapps.kmovieapp.domain.usecase.StartSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Activity-scoped so any screen can start sign-in, while MainActivity opens the browser
 * and receives TMDB's redirect back into the app.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
    private val startSignInUseCase: StartSignInUseCase,
    private val completeSignIn: CompleteSignInUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    val session: StateFlow<Session?> = observeSession()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _events = Channel<SignInEvent>(Channel.BUFFERED)
    val events: Flow<SignInEvent> = _events.receiveAsFlow()

    fun startSignIn() {
        if (_isSigningIn.value) return
        viewModelScope.launch {
            _isSigningIn.value = true
            when (val result = startSignInUseCase()) {
                is Outcome.Success -> _events.send(SignInEvent.OpenApprovalPage(result.data))
                is Outcome.Failure -> _events.send(SignInEvent.Failed(result.error))
            }
            _isSigningIn.value = false
        }
    }

    /** Called with the query parameters TMDB appends to the redirect URI. */
    fun onApprovalResult(requestToken: String?, approved: Boolean) {
        if (requestToken == null || !approved) {
            viewModelScope.launch { _events.send(SignInEvent.Cancelled) }
            return
        }
        viewModelScope.launch {
            _isSigningIn.value = true
            val event = when (val result = completeSignIn(requestToken)) {
                is Outcome.Success -> SignInEvent.SignedIn(result.data.username)
                is Outcome.Failure -> SignInEvent.Failed(result.error)
            }
            _isSigningIn.value = false
            _events.send(event)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _events.send(SignInEvent.SignedOut)
        }
    }
}

sealed interface SignInEvent {
    data class OpenApprovalPage(val url: String) : SignInEvent
    data class SignedIn(val username: String) : SignInEvent
    data object SignedOut : SignInEvent
    data object Cancelled : SignInEvent
    data class Failed(val error: DataError) : SignInEvent
}
