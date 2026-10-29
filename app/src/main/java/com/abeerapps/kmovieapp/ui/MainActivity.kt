package com.abeerapps.kmovieapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.ActivityMainBinding
import com.abeerapps.kmovieapp.databinding.DrawerHeaderBinding
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.model.ThemeMode
import com.abeerapps.kmovieapp.ui.auth.SignInEvent
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.auth.showSignOutConfirmation
import com.abeerapps.kmovieapp.ui.common.applyAsNightMode
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.labelRes
import com.abeerapps.kmovieapp.ui.common.messageRes
import com.abeerapps.kmovieapp.ui.home.HomeFragmentDirections
import com.abeerapps.kmovieapp.ui.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerHost {

    private val signInViewModel: SignInViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerHeader: DrawerHeaderBinding

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

        setUpDrawer()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { signInViewModel.events.collect(::onSignInEvent) }
                launch { signInViewModel.session.collect(::bindAccount) }
                launch {
                    signInViewModel.isSigningIn.collect { signingIn ->
                        drawerHeader.signInButton.isEnabled = !signingIn
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (!handleAuthRedirect(intent)) navController.handleDeepLink(intent)
    }

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun setUpDrawer() {
        val navigationView = binding.navigationView
        drawerHeader = DrawerHeaderBinding.bind(navigationView.getHeaderView(0))
        // The yellow header extends behind the status bar; its content starts below it.
        drawerHeader.root.applySystemBarsPadding(top = true)
        drawerHeader.signInButton.setOnClickListener {
            closeDrawer()
            signInViewModel.startSignIn()
        }
        navigationView.menu.findItem(R.id.drawerVersion).title =
            getString(R.string.version_format, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        navigationView.setNavigationItemSelectedListener { item ->
            closeDrawer()
            when (item.itemId) {
                R.id.drawerFavorites ->
                    navigateFromHome(HomeFragmentDirections.actionHomeToFavorites())
                R.id.drawerTheme -> showThemePicker()
                R.id.drawerAbout -> navigateFromHome(HomeFragmentDirections.actionHomeToAbout())
                R.id.drawerSignOut -> showSignOutConfirmation(signInViewModel::signOut)
            }
            true
        }

        // The drawer belongs to Home; other screens have a back button instead.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.drawerLayout.setDrawerLockMode(
                if (destination.id == R.id.homeFragment) {
                    DrawerLayout.LOCK_MODE_UNLOCKED
                } else {
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                },
            )
        }

        // Back closes an open drawer before it navigates.
        val closeDrawerOnBack = onBackPressedDispatcher.addCallback(this, enabled = false) {
            closeDrawer()
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                closeDrawerOnBack.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: View) {
                closeDrawerOnBack.isEnabled = false
            }
        })
    }

    /** Drawer items navigate from Home, the only screen where the drawer can be opened. */
    private fun navigateFromHome(directions: NavDirections) {
        if (navController.currentDestination?.id == R.id.homeFragment) {
            navController.navigate(directions)
        }
    }

    private fun bindAccount(session: Session?) {
        drawerHeader.accountStatus.text = session
            ?.let { getString(R.string.signed_in_as, it.username) }
            ?: getString(R.string.signed_out_status)
        drawerHeader.signInButton.isVisible = session == null
        binding.navigationView.menu.findItem(R.id.drawerSignOut).isVisible = session != null
    }

    private fun showThemePicker() {
        val modes = ThemeMode.entries
        val labels = modes.map { getString(it.labelRes) }.toTypedArray()
        val selected = modes.indexOf(settingsViewModel.themeMode.value)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.theme)
            .setSingleChoiceItems(labels, selected) { dialog, which ->
                dialog.dismiss()
                val mode = modes[which]
                settingsViewModel.setThemeMode(mode)
                mode.applyAsNightMode()
            }
            .show()
    }

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
