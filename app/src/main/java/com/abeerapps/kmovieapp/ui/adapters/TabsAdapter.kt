package com.abeerapps.kmovieapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abeerapps.kmovieapp.domain.models.GenresModel
import com.abeerapps.kmovieapp.ui.InternalMoviesFragment
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class TabsAdapter(fragment: Fragment, val list: List<GenresModel>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return if (list.isEmpty())
            InternalMoviesFragment(28)
        else
            InternalMoviesFragment(list[position].id)
    }
}