package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.AnnouncementRowLayoutBinding
import com.graduationproject.grad_project.model.Announcement

class AnnouncementRecyclerViewAdapter(
    private val clickListener: (announcement: Announcement?, view: View?) -> Unit
) : ListAdapter<Announcement, AnnouncementRecyclerViewAdapter.AnnouncementViewHolder>(AnnouncementDiffUtil()) {

    class AnnouncementViewHolder(val binding: AnnouncementRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): AnnouncementViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AnnouncementRowLayoutBinding.inflate(layoutInflater, parent, false)
                return AnnouncementViewHolder(binding)
            }
        }
        fun bind(announcement: Announcement, clickListener: (announcement: Announcement?, view: View?) -> Unit) {
            binding.announcement = announcement
            binding.announcementOptions.setOnClickListener {
                clickListener(announcement, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class AnnouncementDiffUtil: DiffUtil.ItemCallback<Announcement>() {
    override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
        return oldItem == newItem
    }
}
