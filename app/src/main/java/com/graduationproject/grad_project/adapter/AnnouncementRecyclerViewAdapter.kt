package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.AnnouncementRowLayoutBinding
import com.graduationproject.grad_project.firebase.AnnouncementOperations
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.view.admin.dialogs.ShowingAnnouncementDialogFragment

class AnnouncementRecyclerViewAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
) : ListAdapter<Announcement, AnnouncementRecyclerViewAdapter.AnnouncementViewHolder>(AnnouncementDiffUtil()) {

    class AnnouncementViewHolder(val binding: AnnouncementRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): AnnouncementViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AnnouncementRowLayoutBinding.inflate(layoutInflater)
                return AnnouncementViewHolder(binding)
            }
        }
        fun bind(announcement: Announcement) {
            binding.announcement = announcement
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.announcementOptions.setOnClickListener { view ->
            val popupMenu = createPopUpMenu(view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.announcementInfo -> {
                        val dialog = ShowingAnnouncementDialogFragment(item)
                        dialog.show(fragmentManager, "announcementDialog")
                        true
                    }
                    R.id.deleteAnnouncement -> {
                        AnnouncementOperations.deleteAnnouncement(item)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun createPopUpMenu(view: View): PopupMenu {
        val popupMenu = PopupMenu(context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu.show()
        return popupMenu
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
