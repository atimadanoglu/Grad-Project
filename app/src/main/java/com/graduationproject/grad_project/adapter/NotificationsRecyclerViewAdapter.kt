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
import com.graduationproject.grad_project.databinding.NotificationRowLayoutBinding
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.view.resident.dialogs.ShowNotificationDialogFragment

class NotificationsRecyclerViewAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
    ) : ListAdapter<Notification, NotificationsRecyclerViewAdapter.NotificationViewHolder>(NotificationDiffUtil()) {

    class NotificationViewHolder(val binding: NotificationRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): NotificationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotificationRowLayoutBinding.inflate(layoutInflater, parent, false)
                return NotificationViewHolder(binding)
            }
        }
        fun bind(notification: Notification) {
            binding.notification = notification
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.notificationOptions.setOnClickListener { view ->
            val popupMenu = createPopUpMenu(view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.announcementInfo -> {
                        val dialog = ShowNotificationDialogFragment(item)
                        dialog.show(fragmentManager, "notificationDialog")
                        true
                    }
                    R.id.deleteAnnouncement -> {
                        NotificationOperations.deleteNotificationInAPosition(item)
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

class NotificationDiffUtil: DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}
