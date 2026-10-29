package com.abeerapps.kmovieapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.ActivityMainBinding
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.ui.auth.SignInEvent
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.drawer.NavigationDrawerController
import com.abeerapps.kmovieapp.ui.settings.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** Hosts navigation, the drawer, and the browser round trip of TMDB sign-in. */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerHost {

    private val signInViewModel: SignInViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: NavigationDrawerController

    private val navController: NavController
        get() {
            val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            return (navHost as NavHostFragment).navController
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Only on first creation: after a configuration change the redirect was already handled.
        if (savedInstanceState == null) handleAuthRedirect(intent)

        drawer = NavigationDrawerController(
            activity = this,
            drawerLayout = binding.drawerLayout,
            navigationView = binding.navigationView,
            navController = { navController },
            signInViewModel = signInViewModel,
            settingsViewModel = settingsViewModel,
        ).also { it.setUp() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signInViewModel.events.collect(::onSignInEvent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (!handleAuthRedirect(intent)) navController.handleDeepLink(intent)
    }

    override fun openDrawer() = drawer.open()

    /** Handles `kmovieapp://auth?request_token=…&approved=true` sent back by TMDB. */
    private fun handleAuthRedirect(intent: Intent): Boolean {
        val uri = intent.data?.takeIf { it.isAuthRedirect() } ?: return false
        signInViewModel.onApprovalResult(
            requestToken = uri.getQueryParameter("request_token"),
            approved = uri.getQueryParameter("approved") == "true",
        )
        return true
    }

    private fun Uri.isAuthRedirect(): Boolean {
        val redirect = BuildConfig.TMDB_AUTH_REDIRECT_URI.toUri()
        return scheme == redirect.scheme && host == redirect.host
    }

    private fun onSignInEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.OpenApprovalPage -> openInCustomTab(event.url)
            is SignInEvent.SignedIn -> showMessage(getString(R.string.signed_in_as, event.username))
            SignInEvent.SignedOut -> showMessage(getString(R.string.signed_out))
            SignInEvent.Cancelled -> showMessage(getString(R.string.sign_in_cancelled))
            is SignInEvent.Failed -> showMessage(
                // During sign-in a 401 means TMDB didn't accept the request token.
                if (event.error == DataError.UNAUTHORIZED) {
                    getString(R.string.sign_in_failed)
                } else {
                    getString(event.error.messageRes())
                },
            )
        }
    }

    private fun openInCustomTab(url: String) {
        try {
            CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(this, url.toUri())
        } catch (_: ActivityNotFoundException) {
            showMessage(getString(R.string.error_no_browser))
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
