package com.abeerapps.kmovieapp.ui.drawer

import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.abeerapps.kmovieapp.BuildConfig
import com.abeerapps.kmovieapp.R
import com.abeerapps.kmovieapp.databinding.DrawerHeaderBinding
import com.abeerapps.kmovieapp.domain.model.Session
import com.abeerapps.kmovieapp.domain.model.ThemeMode
import com.abeerapps.kmovieapp.ui.auth.SignInViewModel
import com.abeerapps.kmovieapp.ui.auth.showSignOutConfirmation
import com.abeerapps.kmovieapp.ui.common.applyAsNightMode
import com.abeerapps.kmovieapp.ui.common.applySystemBarsPadding
import com.abeerapps.kmovieapp.ui.common.labelRes
import com.abeerapps.kmovieapp.ui.home.HomeFragmentDirections
import com.abeerapps.kmovieapp.ui.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

/**
 * Owns the navigation drawer: the account header, menu actions, and on which screens
 * the drawer is available. Keeps MainActivity focused on hosting navigation.
 */
class NavigationDrawerController(
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView,
    private val navController: () -> NavController,
    private val signInViewModel: SignInViewModel,
    private val settingsViewModel: SettingsViewModel,
) {
    private val header = DrawerHeaderBinding.bind(navigationView.getHeaderView(0))

    fun setUp() {
        // The yellow header extends behind the status bar; its content starts below it.
        header.root.applySystemBarsPadding(top = true)
        header.signInButton.setOnClickListener {
            close()
            signInViewModel.startSignIn()
        }
        navigationView.menu.findItem(R.id.drawerVersion).title = activity.getString(
            R.string.version_format,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
        )
        navigationView.setNavigationItemSelectedListener { item ->
            close()
            onItemSelected(item.itemId)
            true
        }

        // The drawer belongs to Home; other screens have a back button instead.
        navController().addOnDestinationChangedListener { _, destination, _ ->
            drawerLayout.setDrawerLockMode(
                if (destination.id == R.id.homeFragment) {
                    DrawerLayout.LOCK_MODE_UNLOCKED
                } else {
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                },
            )
        }

        // Back closes an open drawer before it navigates.
        val closeOnBack = activity.onBackPressedDispatcher.addCallback(activity, enabled = false) {
            close()
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                closeOnBack.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: View) {
                closeOnBack.isEnabled = false
            }
        })

        activity.lifecycleScope.launch {
            activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { signInViewModel.session.collect(::bindAccount) }
                launch {
                    signInViewModel.isSigningIn.collect { signingIn ->
                        header.signInButton.isEnabled = !signingIn
                    }
                }
            }
        }
    }

    fun open() = drawerLayout.openDrawer(GravityCompat.START)

    private fun close() = drawerLayout.closeDrawer(GravityCompat.START)

    private fun onItemSelected(itemId: Int) {
        when (itemId) {
            R.id.drawerFavorites -> navigateFromHome(HomeFragmentDirections.actionHomeToFavorites())
            R.id.drawerTheme -> showThemePicker()
            R.id.drawerAbout -> navigateFromHome(HomeFragmentDirections.actionHomeToAbout())
            R.id.drawerSignOut -> activity.showSignOutConfirmation(signInViewModel::signOut)
        }
    }

    /** Drawer items navigate from Home, the only screen where the drawer can be opened. */
    private fun navigateFromHome(directions: NavDirections) {
        val controller = navController()
        if (controller.currentDestination?.id == R.id.homeFragment) controller.navigate(directions)
    }

    private fun bindAccount(session: Session?) {
        header.accountStatus.text = session
            ?.let { activity.getString(R.string.signed_in_as, it.username) }
            ?: activity.getString(R.string.signed_out_status)
        header.signInButton.isVisible = session == null
        navigationView.menu.findItem(R.id.drawerSignOut).isVisible = session != null
    }

    private fun showThemePicker() {
        val modes = ThemeMode.entries
        val labels = modes.map { activity.getString(it.labelRes) }.toTypedArray()
        val selected = modes.indexOf(settingsViewModel.themeMode.value)
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.theme)
            .setSingleChoiceItems(labels, selected) { dialog, which ->
                dialog.dismiss()
                val mode = modes[which]
                settingsViewModel.setThemeMode(mode)
                mode.applyAsNightMode()
            }
            .show()
    }
}
