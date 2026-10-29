package com.abeerapps.kmovieapp.ui.genre

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.abeerapps.kmovieapp.databinding.FragmentGenreMoviesBinding
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

@AndroidEntryPoint
class GenreMoviesFragment :
    BaseFragment<FragmentGenreMoviesBinding>(FragmentGenreMoviesBinding::inflate) {

    private val viewModel: GenreMoviesViewModel by viewModels()

    private val movieAdapter = MovieGridAdapter { navigateToMovieDetails(it.id) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.movieList.applySystemBarsPadding(bottom = true)

        binding.movieList.setUpPagedMovieGrid(movieAdapter)
        setUpListeners()
        observeMovies()
    }

    private fun setUpListeners() = with(binding) {
        swipeRefresh.setOnRefreshListener(movieAdapter::refresh)
        errorView.setOnRetryClickListener(movieAdapter::retry)
    }

    private fun observeMovies() {
        collectLatestWithViewLifecycle(viewModel.movies, movieAdapter::submitData)
        collectWithViewLifecycle(movieAdapter.loadStateFlow, ::render)
    }

    private fun render(loadStates: CombinedLoadStates) = with(binding) {
        val hasItems = movieAdapter.itemCount > 0
        val state = loadStates.toPagedListState(movieAdapter.itemCount)

        // Pull-to-refresh shows refreshes of a filled list; the full-screen spinner the first load.
        swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading && hasItems
        progressIndicator.isVisible = state.isLoading
        emptyMessage.isVisible = state.isEmpty
        errorView.render(state.error)
    }

    override fun onDestroyView() {
        binding.movieList.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(genreId: Int) = GenreMoviesFragment().apply {
            arguments = bundleOf(GenreMoviesViewModel.ARG_GENRE_ID to genreId)
        }
    }
}
