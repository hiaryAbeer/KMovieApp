package com.abeerapps.kmovieapp.ui.genre

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentGenreMoviesBinding
import com.abeerapps.kmovieapp.ui.common.MovieGridAdapter
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.collectLatestWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.collectWithViewLifecycle
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.common.navigateToMovieDetails
import com.abeerapps.kmovieapp.ui.common.setUpPagedMovieGrid
import com.abeerapps.kmovieapp.ui.common.toPagedListState
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
        binding.movieList.setUpPagedMovieGrid(adapter)
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
        binding.errorView.retryButton.setOnClickListener { adapter.retry() }

        collectLatestWithViewLifecycle(viewModel.movies, adapter::submitData)
        collectWithViewLifecycle(adapter.loadStateFlow) { loadStates ->
            val state = loadStates.toPagedListState(adapter.itemCount)
            binding.swipeRefresh.isRefreshing =
                loadStates.refresh is LoadState.Loading && adapter.itemCount > 0
            binding.progressIndicator.isVisible = state.isLoading
            binding.emptyMessage.isVisible = state.isEmpty
            binding.errorView.root.isVisible = state.error != null
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
