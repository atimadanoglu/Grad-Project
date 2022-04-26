package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.VotingContinuesAdminItemBinding
import com.graduationproject.grad_project.model.Voting
import java.util.*

class ContinuesVotingAdminAdapter:
    ListAdapter<Voting, ContinuesVotingAdminAdapter.VotingViewHolder>(VotingDiffUtil()) {

    class VotingViewHolder(val binding: VotingContinuesAdminItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewHolder = this
        }
        companion object {
            fun inflateFrom(parent: ViewGroup): VotingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VotingContinuesAdminItemBinding.inflate(layoutInflater)
                return VotingViewHolder(binding)
            }
        }

        fun bind(voting: Voting) {
            calculateDuration(voting)
            binding.voting = voting
            binding.executePendingBindings()
        }

        fun calculateDuration(voting: Voting): String {
            val date = Date()
            val diff: Long = voting.date - date.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            return "$hours saat"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingViewHolder {
        return VotingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: VotingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}

class VotingDiffUtil: DiffUtil.ItemCallback<Voting>() {
    override fun areItemsTheSame(oldItem: Voting, newItem: Voting): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Voting, newItem: Voting): Boolean {
        return oldItem == newItem
    }
}
