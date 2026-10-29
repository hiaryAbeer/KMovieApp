package com.abeerapps.kmovieapp.ui.login

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentLoginBinding
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.common.BaseFragment
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.openInCustomTab
import com.abeerapps.kmovieapp.ui.theme.KMovieTheme
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Hosts the Compose [LoginScreen] inside the XML/Fragment app. Both sign-in methods end with
 * a stored session, so the screen closes itself as soon as one exists.
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()
    private val signInViewModel: SignInViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpComposeContent()
        observeSignIn()
    }

    private fun setUpComposeContent() = with(binding.composeView) {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val isWebSignInStarting by signInViewModel.isSigningIn.collectAsStateWithLifecycle()
            KMovieTheme {
                LoginScreen(
                    state = state,
                    isWebSignInStarting = isWebSignInStarting,
                    onBack = { findNavController().navigateUp() },
                    onUsernameChange = viewModel::onUsernameChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                    onSubmit = viewModel::onSubmit,
                    onContinueWithWebsite = signInViewModel::startSignIn,
                    onCreateAccount = ::openSignUpPage,
                )
            }
        }
    }

    private fun observeSignIn() {
        collectWithViewLifecycle(viewModel.signedIn, ::announceSignedIn)
        collectWithViewLifecycle(signInViewModel.session) { session ->
            if (session != null) close()
        }
    }

    /** The website flow is announced by MainActivity; this covers the credentials flow. */
    private fun announceSignedIn(session: Session) {
        val root = requireActivity().findViewById<View>(android.R.id.content)
        val message = getString(R.string.signed_in_as, session.username)
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun close() {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.loginFragment) navController.navigateUp()
    }

    /** TMDB has no sign-up API, so accounts are created on its website. */
    private fun openSignUpPage() {
        if (!requireContext().openInCustomTab(SIGN_UP_URL)) showMessage(R.string.error_no_browser)
    }

    private companion object {
        const val SIGN_UP_URL = "https://www.themoviedb.org/signup"
    }
}
