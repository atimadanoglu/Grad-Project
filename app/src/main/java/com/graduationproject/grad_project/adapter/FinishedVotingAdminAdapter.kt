package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.VotingDoneAdminItemBinding
import com.graduationproject.grad_project.model.Voting

class FinishedVotingAdminAdapter:
    ListAdapter<Voting, FinishedVotingAdminAdapter.FinishedVotingViewHolder>(FinishedVotingDiffUtil()) {

    class FinishedVotingViewHolder(val binding: VotingDoneAdminItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): FinishedVotingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VotingDoneAdminItemBinding.inflate(layoutInflater)
                return FinishedVotingViewHolder(binding)
            }
        }
        fun bind(voting: Voting) {
            binding.voting = voting
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedVotingViewHolder {
        return FinishedVotingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: FinishedVotingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        calculateRate(item)
        displayYesOrNo(item)
    }

    fun calculateRate(voting: Voting): String {
        return ""
    }

    fun displayYesOrNo(voting: Voting): String {
        return ""
    }

}

class FinishedVotingDiffUtil: DiffUtil.ItemCallback<Voting>() {
    override fun areItemsTheSame(oldItem: Voting, newItem: Voting): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Voting, newItem: Voting): Boolean {
        return oldItem == newItem
    }

}
