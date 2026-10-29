package com.abeerapps.kmovieapp.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import com.abeerapps.kmovieapp.databinding.FragmentFavoritesBinding
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.auth.showSignOutConfirmation
import com.abeerapps.kmovieapp.ui.common.BaseFragment
import com.abeerapps.kmovieapp.ui.common.MovieGridAdapter
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectLatestWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.navigateToMovieDetails
import com.abeerapps.kmovieapp.ui.common.render
import com.abeerapps.kmovieapp.ui.common.setOnRetryClickListener
import com.abeerapps.kmovieapp.ui.common.setUpPagedMovieGrid
import com.abeerapps.kmovieapp.ui.common.toPagedListState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class FavoritesFragment :
    BaseFragment<FragmentFavoritesBinding>(FragmentFavoritesBinding::inflate) {

    private val viewModel: FavoritesViewModel by viewModels()
    private val signInViewModel: SignInViewModel by activityViewModels()

    private val movieAdapter = MovieGridAdapter { navigateToMovieDetails(it.id) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topBar.applySystemBarsPadding(top = true)
        binding.movieList.applySystemBarsPadding(bottom = true)

        binding.movieList.setUpPagedMovieGrid(movieAdapter)
        setUpClickListeners()
        observeFavorites()
        observeSignIn()
    }

    private fun setUpClickListeners() = with(binding) {
        backButton.setOnClickListener { findNavController().navigateUp() }
        signInButton.setOnClickListener { signInViewModel.startSignIn() }
        signOutButton.setOnClickListener {
            requireContext().showSignOutConfirmation(signInViewModel::signOut)
        }
        errorView.setOnRetryClickListener(movieAdapter::retry)
    }

    private fun observeFavorites() {
        collectLatestWithViewLifecycle(viewModel.favorites, movieAdapter::submitData)
    }

    private fun observeSignIn() {
        collectWithViewLifecycle(signInViewModel.isSigningIn) { signingIn ->
            binding.signInButton.isEnabled = !signingIn
        }
        val sessionAndLoadStates =
            signInViewModel.session.combine(movieAdapter.loadStateFlow, ::Pair)
        collectWithViewLifecycle(sessionAndLoadStates) { (session, loadStates) ->
            render(session, loadStates)
        }
    }

    private fun render(session: Session?, loadStates: CombinedLoadStates) = with(binding) {
        val isSignedIn = session != null
        val state = loadStates.toPagedListState(movieAdapter.itemCount)

        subtitle.isVisible = isSignedIn
        subtitle.text = session?.username
        signOutButton.isVisible = isSignedIn
        signedOutGroup.isVisible = !isSignedIn
        movieList.isVisible = isSignedIn
        progressIndicator.isVisible = isSignedIn && state.isLoading
        emptyMessage.isVisible = isSignedIn && state.isEmpty
        errorView.render(state.error.takeIf { isSignedIn })
    }

    override fun onDestroyView() {
        binding.movieList.adapter = null
        super.onDestroyView()
    }
}
