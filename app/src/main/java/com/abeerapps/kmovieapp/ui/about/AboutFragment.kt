package com.abeerapps.kmovieapp.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentAboutBinding
import com.abeerapps.kmovieapp.ui.common.BaseFragment
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding

/** Static screen: version, attribution and the libraries the app is built with. */
class AboutFragment : BaseFragment<FragmentAboutBinding>(FragmentAboutBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding(top = true, bottom = true)

        showVersion()
        setUpClickListeners()
    }

    private fun showVersion() {
        binding.version.text =
            getString(R.string.version_format, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

    private fun setUpClickListeners() = with(binding) {
        backButton.setOnClickListener { findNavController().navigateUp() }
        visitTmdbButton.setOnClickListener { openTmdbWebsite() }
    }

    private fun openTmdbWebsite() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, TMDB_URL.toUri()))
        } catch (_: ActivityNotFoundException) {
            showMessage(R.string.error_no_browser)
        }
    }

    private companion object {
        const val TMDB_URL = "https://www.themoviedb.org"
    }
}
