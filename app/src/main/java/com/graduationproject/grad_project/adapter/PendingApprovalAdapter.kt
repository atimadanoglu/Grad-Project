package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.PendingItemBinding
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.SiteResident

class PendingApprovalAdapter:
    ListAdapter<SiteResident, PendingApprovalAdapter.PendingApprovalViewHolder>(PendingApprovalDiffUtil()) {
    class PendingApprovalViewHolder(val binding: PendingItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): PendingApprovalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PendingItemBinding.inflate(layoutInflater)
                return PendingApprovalViewHolder(binding)
            }
        }
        fun bind(siteResident: SiteResident) {
            binding.siteResident = siteResident
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingApprovalViewHolder {
        return PendingApprovalViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: PendingApprovalViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.binding.acceptButton.setOnClickListener {

        }
    }

}

class PendingApprovalDiffUtil: DiffUtil.ItemCallback<SiteResident>() {
    override fun areItemsTheSame(oldItem: SiteResident, newItem: SiteResident): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: SiteResident, newItem: SiteResident): Boolean {
        return oldItem == newItem
    }
}
