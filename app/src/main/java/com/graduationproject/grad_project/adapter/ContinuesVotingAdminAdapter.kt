package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.VotingContinuesAdminItemBinding
import com.graduationproject.grad_project.model.Voting

class ContinuesVotingAdminAdapter:
    ListAdapter<Voting, ContinuesVotingAdminAdapter.VotingViewHolder>(VotingDiffUtil()) {

    class VotingViewHolder(val binding: VotingContinuesAdminItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): VotingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VotingContinuesAdminItemBinding.inflate(layoutInflater)
                return VotingViewHolder(binding)
            }
        }
        fun bind(voting: Voting) {
            binding.voting = voting
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingViewHolder {
        return VotingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: VotingViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.adapter = this
        holder.bind(item)
        calculateDuration(item)
    }

    fun calculateDuration(voting: Voting): String {
        return ""
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
