package com.abeerapps.kmovieapp.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Base for every screen: owns the ViewBinding for the lifetime of the fragment's view.
 *
 * [binding] is available from onViewCreated until `super.onDestroyView()`. Subclasses that
 * release views in onDestroyView (e.g. detaching adapters) must do it before calling super.
 * Keeping the binding only while the view exists is what prevents leaking the view hierarchy
 * while the fragment sits on the back stack.
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = checkNotNull(_binding) {
            "${javaClass.simpleName} used its binding outside the view lifecycle"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflate(inflater, container, false).also { _binding = it }.root

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun showMessage(@StringRes messageRes: Int) {
        Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_SHORT).show()
    }
}
