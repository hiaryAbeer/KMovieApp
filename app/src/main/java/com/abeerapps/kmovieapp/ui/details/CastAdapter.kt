package com.abeerapps.kmovieapp.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abeerapps.kmovieapp.databinding.ItemCastBinding
import com.abeerapps.kmovieapp.domain.model.CastMember
import com.abeerapps.kmovieapp.ui.common.TmdbImageSize
import com.abeerapps.kmovieapp.ui.common.loadTmdbImage

class CastAdapter : ListAdapter<CastMember, CastAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: CastMember) = with(binding) {
            name.text = member.name
            character.text = member.character
            photo.contentDescription = member.name
            photo.loadTmdbImage(member.profilePath, TmdbImageSize.PROFILE)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CastMember>() {
        override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember) =
            oldItem == newItem
    }
}
