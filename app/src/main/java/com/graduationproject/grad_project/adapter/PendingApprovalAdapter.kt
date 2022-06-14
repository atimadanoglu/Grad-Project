package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.PendingItemBinding
import com.graduationproject.grad_project.model.SiteResident

class PendingApprovalAdapter(
    private val phoneTextClickListener: (phoneNumber: String) -> Unit,
    private val acceptButtonClickListener: (siteResident: SiteResident) -> Unit,
    private val rejectButtonClickListener: (siteResident: SiteResident) -> Unit
):
    ListAdapter<SiteResident, PendingApprovalAdapter.PendingApprovalViewHolder>(PendingApprovalDiffUtil()) {
    class PendingApprovalViewHolder(val binding: PendingItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): PendingApprovalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PendingItemBinding.inflate(layoutInflater, parent, false)
                return PendingApprovalViewHolder(binding)
            }
        }
        fun bind(
            siteResident: SiteResident,
            phoneTextClickListener: (phoneNumber: String) -> Unit,
            acceptButtonClickListener: (siteResident: SiteResident) -> Unit,
            rejectButtonClickListener: (siteResident: SiteResident) -> Unit
        ) {
            binding.siteResident = siteResident
            binding.phoneText.setOnClickListener {
                phoneTextClickListener(siteResident.phoneNumber)
            }
            binding.acceptButton.setOnClickListener {
                acceptButtonClickListener(siteResident)
            }
            binding.rejectButton.setOnClickListener {
                rejectButtonClickListener(siteResident)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingApprovalViewHolder {
        return PendingApprovalViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: PendingApprovalViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, phoneTextClickListener, acceptButtonClickListener, rejectButtonClickListener)
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
