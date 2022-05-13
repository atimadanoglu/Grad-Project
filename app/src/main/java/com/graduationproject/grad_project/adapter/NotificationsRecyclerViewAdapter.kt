package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.NotificationRowLayoutBinding
import com.graduationproject.grad_project.model.Notification

class NotificationsRecyclerViewAdapter(
    private val clickListener: (notification: Notification?, view: View?) -> Unit
) : ListAdapter<Notification, NotificationsRecyclerViewAdapter.NotificationViewHolder>(NotificationDiffUtil()) {

    class NotificationViewHolder(val binding: NotificationRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): NotificationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotificationRowLayoutBinding.inflate(layoutInflater, parent, false)
                return NotificationViewHolder(binding)
            }
        }
        fun bind(notification: Notification, clickListener: (notification: Notification?, view: View?) -> Unit) {
            binding.notification = notification
            binding.notificationOptions.setOnClickListener {
                clickListener(notification, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
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
