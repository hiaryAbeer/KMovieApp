package com.abeerapps.kmovieapp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentSearchBinding
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
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        binding.topBar.applySystemBarsPadding(top = true)
        binding.movieList.applySystemBarsPadding(bottom = true, includeIme = true)

        val adapter = MovieGridAdapter {
            hideKeyboard()
            navigateToMovieDetails(it.id)
        }
        binding.movieList.setUpPagedMovieGrid(adapter)
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.errorView.retryButton.setOnClickListener { adapter.retry() }

        binding.searchInput.setText(viewModel.query.value)
        binding.searchInput.doAfterTextChanged { viewModel.onQueryChange(it?.toString().orEmpty()) }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            (actionId == EditorInfo.IME_ACTION_SEARCH).also { if (it) hideKeyboard() }
        }
        if (savedInstanceState == null && viewModel.query.value.isEmpty()) showKeyboard()

        collectLatestWithViewLifecycle(viewModel.results, adapter::submitData)
        val queryAndLoadStates = viewModel.query.combine(adapter.loadStateFlow, ::Pair)
        collectWithViewLifecycle(queryAndLoadStates) { (rawQuery, loadStates) ->
            val query = rawQuery.trim()
            val hasQuery = query.length >= SearchViewModel.MIN_QUERY_LENGTH
            val state = loadStates.toPagedListState(adapter.itemCount)

            binding.hintMessage.isVisible = !hasQuery
            binding.progressIndicator.isVisible = hasQuery && state.isLoading
            binding.emptyMessage.isVisible = hasQuery && state.isEmpty
            binding.emptyMessage.text = getString(R.string.search_no_results, query)
            binding.errorView.root.isVisible = hasQuery && state.error != null
            state.error?.let { binding.errorView.errorMessage.setText(it.messageRes()) }
        }
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
        _binding = null
        super.onDestroyView()
    }
}
