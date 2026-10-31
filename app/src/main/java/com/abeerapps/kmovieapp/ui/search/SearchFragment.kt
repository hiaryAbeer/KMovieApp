package com.abeerapps.kmovieapp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentSearchBinding
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
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()

    private val movieAdapter = MovieGridAdapter {
        hideKeyboard()
        navigateToMovieDetails(it.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topBar.applySystemBarsPadding(top = true)
        binding.movieList.applySystemBarsPadding(bottom = true, includeIme = true)

        binding.movieList.setUpPagedMovieGrid(movieAdapter)
        setUpSearchInput(isFirstOpen = savedInstanceState == null)
        setUpClickListeners()
        observeResults()
    }

    private fun setUpSearchInput(isFirstOpen: Boolean) = with(binding.searchInput) {
        setText(viewModel.query.value)
        doAfterTextChanged { viewModel.onQueryChange(it?.toString().orEmpty()) }
        setOnEditorActionListener { _, actionId, _ ->
            val isSearch = actionId == EditorInfo.IME_ACTION_SEARCH
            if (isSearch) hideKeyboard()
            isSearch
        }
        if (isFirstOpen && viewModel.query.value.isEmpty()) showKeyboard()
    }

    private fun setUpClickListeners() = with(binding) {
        backButton.setOnClickListener { findNavController().navigateUp() }
        errorView.setOnRetryClickListener(movieAdapter::retry)
    }

    private fun observeResults() {
        collectLatestWithViewLifecycle(viewModel.results, movieAdapter::submitData)
        val queryAndLoadStates = viewModel.query.combine(movieAdapter.loadStateFlow, ::Pair)
        collectWithViewLifecycle(queryAndLoadStates) { (query, loadStates) ->
            render(query, loadStates)
        }
    }

    private fun render(rawQuery: String, loadStates: CombinedLoadStates) = with(binding) {
        val query = rawQuery.trim()
        val hasQuery = query.length >= SearchViewModel.MIN_QUERY_LENGTH
        val state = loadStates.toPagedListState(movieAdapter.itemCount)

        hintMessage.isVisible = !hasQuery
        progressIndicator.isVisible = hasQuery && state.isLoading
        emptyMessage.isVisible = hasQuery && state.isEmpty
        emptyMessage.text = getString(R.string.search_no_results, query)
        errorView.render(state.error.takeIf { hasQuery })
    }

    private fun showKeyboard() {
        binding.searchInput.requestFocus()
        insetsController().show(WindowInsetsCompat.Type.ime())
    }

    private fun hideKeyboard() {
        insetsController().hide(WindowInsetsCompat.Type.ime())
    }

    private fun insetsController() =
        WindowCompat.getInsetsController(requireActivity().window, binding.searchInput)

    override fun onDestroyView() {
        binding.movieList.adapter = null
        super.onDestroyView()
    }
}
