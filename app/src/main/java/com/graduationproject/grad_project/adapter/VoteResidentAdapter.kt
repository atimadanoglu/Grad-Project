package com.graduationproject.grad_project.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.VotingContinuesAdminItemBinding
import com.graduationproject.grad_project.model.Voting
import com.graduationproject.grad_project.view.resident.dialogs.ShowVoteResidentDialogFragment
import java.util.*

class VoteResidentAdapter(
    private val fragmentManager: FragmentManager
): ListAdapter<Voting, VoteResidentAdapter.VotingViewHolder>(VoteResidentDiffUtil()) {

    private lateinit var auth: FirebaseAuth
    class VotingViewHolder(val binding: VotingContinuesAdminItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): VotingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VotingContinuesAdminItemBinding.inflate(layoutInflater, parent, false)
                return VotingViewHolder(binding)
            }
        }
        fun bind(voting: Voting) {
            binding.duration.text = calculateDuration(voting)
            binding.voting = voting
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingViewHolder {
        return VotingViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: VotingViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        auth = FirebaseAuth.getInstance()
        val filtered = item.residentsWhoVoted.filter {
            it?.id == auth.currentUser?.email
        }
        if (filtered.isNotEmpty()) {
            holder.binding.root.isEnabled = false
            holder.binding.constraintOfCardViewVoting
                .setBackgroundColor(Color.parseColor("#3B284443"))
        }

        if (filtered.isEmpty() || item.residentsWhoVoted.isEmpty()) {
            holder.binding.root.setOnClickListener {
                val dialog = ShowVoteResidentDialogFragment(item)
                dialog.show(fragmentManager, "showVoteResidentDialog")
                holder.binding.executePendingBindings()
            }
        }
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
