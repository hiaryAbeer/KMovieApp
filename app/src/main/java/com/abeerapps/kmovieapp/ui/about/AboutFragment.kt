package com.abeerapps.kmovieapp.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.FragmentAboutBinding
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.google.android.material.snackbar.Snackbar

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAboutBinding.bind(view)
        binding.root.applySystemBarsPadding(top = true, bottom = true)

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        binding.version.text =
            getString(R.string.version_format, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        binding.visitTmdbButton.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, TMDB_URL.toUri()))
            } catch (_: ActivityNotFoundException) {
                Snackbar.make(view, R.string.error_no_browser, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private companion object {
        const val TMDB_URL = "https://www.themoviedb.org"
    }
}
