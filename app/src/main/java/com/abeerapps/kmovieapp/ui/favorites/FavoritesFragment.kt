package com.abeerapps.kmovieapp.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentFavoritesBinding
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.auth.showSignOutConfirmation
import com.abeerapps.kmovieapp.ui.common.MovieGridAdapter
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectLatestWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.common.navigateToMovieDetails
import com.abeerapps.kmovieapp.ui.common.setUpPagedMovieGrid
import com.abeerapps.kmovieapp.ui.common.toPagedListState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels()
    private val signInViewModel: SignInViewModel by activityViewModels()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)
        binding.topBar.applySystemBarsPadding(top = true)
        binding.movieList.applySystemBarsPadding(bottom = true)

        val adapter = MovieGridAdapter { navigateToMovieDetails(it.id) }
        binding.movieList.setUpPagedMovieGrid(adapter)
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.signOutButton.setOnClickListener {
            requireContext().showSignOutConfirmation(signInViewModel::signOut)
        }
        binding.signInButton.setOnClickListener { signInViewModel.startSignIn() }
        binding.errorView.retryButton.setOnClickListener { adapter.retry() }

        collectLatestWithViewLifecycle(viewModel.favorites, adapter::submitData)
        collectWithViewLifecycle(signInViewModel.isSigningIn) { signingIn ->
            binding.signInButton.isEnabled = !signingIn
        }

        val sessionAndLoadStates = signInViewModel.session.combine(adapter.loadStateFlow, ::Pair)
        collectWithViewLifecycle(sessionAndLoadStates) { (session, loadStates) ->
            val isSignedIn = session != null
            val state = loadStates.toPagedListState(adapter.itemCount)

            binding.subtitle.isVisible = isSignedIn
            binding.subtitle.text = session?.username
            binding.signOutButton.isVisible = isSignedIn
            binding.signedOutGroup.isVisible = !isSignedIn
            binding.movieList.isVisible = isSignedIn
            binding.progressIndicator.isVisible = isSignedIn && state.isLoading
            binding.emptyMessage.isVisible = isSignedIn && state.isEmpty
            binding.errorView.root.isVisible = isSignedIn && state.error != null
            state.error?.let { binding.errorView.errorMessage.setText(it.messageRes()) }
        }
    }

    override fun onDestroyView() {
        binding.movieList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
