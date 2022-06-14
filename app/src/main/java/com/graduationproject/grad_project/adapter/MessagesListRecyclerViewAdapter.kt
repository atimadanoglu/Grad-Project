package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.MessageRowLayoutBinding
import com.graduationproject.grad_project.model.Message

class MessagesListRecyclerViewAdapter(
    private val clickListener: (message: Message, anchor: View) -> Unit
): ListAdapter<Message, MessagesListRecyclerViewAdapter.MessageViewHolder>(MessagesDiffUtil()) {

    class MessageViewHolder(val binding: MessageRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): MessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MessageRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MessageViewHolder(binding)
            }
        }
        fun bind(message: Message, clickListener: (message: Message, anchor: View) -> Unit) {
            binding.message = message
            binding.messageOptions.setOnClickListener {
                clickListener(message, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
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

