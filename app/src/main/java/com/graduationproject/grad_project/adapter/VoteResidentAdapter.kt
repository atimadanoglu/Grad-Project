package com.graduationproject.grad_project.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.databinding.VotingContinuesAdminItemBinding
import com.graduationproject.grad_project.model.Voting
import java.util.*

class VoteResidentAdapter(
    private val clickListener: (voting: Voting) -> Unit
): ListAdapter<Voting, VoteResidentAdapter.VotingViewHolder>(VoteResidentDiffUtil()) {

    class VotingViewHolder(val binding: VotingContinuesAdminItemBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var auth: FirebaseAuth
        companion object {
            fun inflateFrom(parent: ViewGroup): VotingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VotingContinuesAdminItemBinding.inflate(layoutInflater, parent, false)
                return VotingViewHolder(binding)
            }
        }
        fun bind(voting: Voting, clickListener: (voting: Voting) -> Unit) {
            binding.duration.text = calculateDuration(voting)
            binding.voting = voting
            setIsVoted(voting, clickListener)
            binding.executePendingBindings()
        }

        private fun calculateDuration(voting: Voting): String {
            val date = Date()
            val diff: Long = voting.date - date.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            return "$hours (s)"
        }

        private fun setIsVoted(item: Voting, clickListener: (voting: Voting) -> Unit) {
            auth = FirebaseAuth.getInstance()
            val filtered = item.residentsWhoVoted.filter {
                it?.id == auth.currentUser?.email
            }
            if (filtered.isNotEmpty()) {
                binding.root.isEnabled = false
                binding.constraintOfCardViewVoting
                    .setBackgroundColor(Color.parseColor("#3B284443"))
            }

            if (filtered.isEmpty() || item.residentsWhoVoted.isEmpty()) {
                binding.root.setOnClickListener {
                    clickListener(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingViewHolder {
        return VotingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: VotingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class VoteResidentDiffUtil: DiffUtil.ItemCallback<Voting>() {
    override fun areItemsTheSame(oldItem: Voting, newItem: Voting): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Voting, newItem: Voting): Boolean {
        return oldItem == newItem
    }
}
