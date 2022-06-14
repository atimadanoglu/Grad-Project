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
                val binding = VotingDoneAdminItemBinding.inflate(layoutInflater, parent, false)
                return FinishedVotingViewHolder(binding)
            }
        }
        fun bind(voting: Voting) {
            binding.resultText.text = displayYesOrNo(voting)
            binding.resultRate.text = calculateRate(voting)
            binding.voting = voting
            binding.executePendingBindings()
        }

        private fun displayYesOrNo(voting: Voting): String {
            val operation = voting.totalYes - voting.totalNo
            val result = when {
                operation > 0 -> "Evet"
                operation < 0 -> "Hayır"
                else -> "Eşit"
            }
            return result
        }

        private fun calculateRate(voting: Voting): String {
            val noCount = voting.totalNo
            val yesCount = voting.totalYes
            val rate = when {
                yesCount > noCount -> "%${(yesCount.toFloat() / (yesCount + noCount).toFloat() * 100).toInt()}"
                noCount > yesCount -> "%${(noCount.toFloat() / (yesCount + noCount).toFloat() * 100).toInt()}"
                else -> "%50"
            }
            return rate
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedVotingViewHolder {
        return FinishedVotingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: FinishedVotingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
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
