package com.abeerapps.kmovieapp.ui.genre

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentGenreMoviesBinding
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.common.navigateToMovieDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenreMoviesFragment : Fragment(R.layout.fragment_genre_movies) {

    private val viewModel: GenreMoviesViewModel by viewModels()

    private var _binding: FragmentGenreMoviesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGenreMoviesBinding.bind(view)
        binding.movieList.applySystemBarsPadding(bottom = true)

        val adapter = MovieGridAdapter { navigateToMovieDetails(it.id) }
        binding.movieList.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.errorView.retryButton.setOnClickListener { viewModel.retry() }

        collectWithViewLifecycle(viewModel.uiState) { state ->
            adapter.submitList(state.movies)

            val showFullScreenError = state.error != null && state.movies.isEmpty()
            binding.swipeRefresh.isRefreshing = state.isRefreshing
            binding.progressIndicator.isVisible = state.isLoading
            binding.emptyMessage.isVisible = state.isEmpty
            binding.errorView.root.isVisible = showFullScreenError
            state.error?.let { binding.errorView.errorMessage.setText(it.messageRes()) }
        }
    }

    override fun onDestroyView() {
        binding.movieList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_GENRE_ID = "genre_id"

        fun newInstance(genreId: Int) = GenreMoviesFragment().apply {
            arguments = bundleOf(ARG_GENRE_ID to genreId)
        }
    }
}
