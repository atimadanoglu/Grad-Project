package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.MeetingItemBinding
import com.graduationproject.grad_project.model.Meeting

class MeetingAdminAdapter(
    private val clickListener: (id: Meeting, view: View) -> Unit
): ListAdapter<Meeting, MeetingAdminAdapter.MeetingViewHolder>(MeetingDiffUtil()) {
    class MeetingViewHolder(private val binding: MeetingItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): MeetingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MeetingItemBinding.inflate(layoutInflater, parent, false)
                return MeetingViewHolder(binding)
            }
        }
        fun bind(meeting: Meeting, position: Int, clickListener: (id: Meeting, view: View) -> Unit) {
            setVisibilityOfView(position, binding.openMeetingImage)
            binding.meeting = meeting
            binding.openMeetingImage.setOnClickListener {
                clickListener(meeting, it)
            }
            binding.executePendingBindings()
        }

        private fun setVisibilityOfView(position: Int, view: View) {
            if (position != 0) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        return MeetingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position, clickListener)
    }
}

class MeetingDiffUtil: DiffUtil.ItemCallback<Meeting>() {
    override fun areItemsTheSame(oldItem: Meeting, newItem: Meeting): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Meeting, newItem: Meeting): Boolean {
        return oldItem == newItem
    }
}
