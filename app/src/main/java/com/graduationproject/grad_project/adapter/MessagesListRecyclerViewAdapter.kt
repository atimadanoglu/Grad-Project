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
import com.graduationproject.grad_project.databinding.MessageRowLayoutBinding
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.model.Message
import com.graduationproject.grad_project.view.resident.dialogs.ShowMessageDialogFragment

class MessagesListRecyclerViewAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
): ListAdapter<Message, MessagesListRecyclerViewAdapter.MessageViewHolder>(MessagesDiffUtil()) {

    class MessageViewHolder(val binding: MessageRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): MessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MessageRowLayoutBinding.inflate(layoutInflater)
                return MessageViewHolder(binding)
            }
        }
        fun bind(message: Message) {
            binding.message = message
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.messageOptions.setOnClickListener { view ->
            val popupMenu = createPopUpMenu(view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.announcementInfo -> {
                        val showMessageDialogFragment = ShowMessageDialogFragment(item)
                        showMessageDialogFragment.show(fragmentManager, "dialog")
                        true
                    }
                    R.id.deleteAnnouncement -> {
                        MessagesOperations.deleteMessageInSpecificPosition(item)
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

class MessagesDiffUtil: DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}

