package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.PendingItemBinding
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.SiteResident

class PendingApprovalAdapter(
    private val context: Context,
    private val clickListener: (phoneNumber: String) -> Unit
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
        fun bind(siteResident: SiteResident, clickListener: (phoneNumber: String) -> Unit) {
            binding.siteResident = siteResident
            binding.phoneText.setOnClickListener {
                clickListener(siteResident.phoneNumber)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingApprovalViewHolder {
        return PendingApprovalViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: PendingApprovalViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
        holder.binding.acceptButton.setOnClickListener {
            showAlertMessageForAcceptButton(item)
        }
        holder.binding.rejectButton.setOnClickListener {
            showAlertMessageForRejectButton(item)
        }
    }
    private fun showAlertMessageForAcceptButton(resident: SiteResident) {
        val text = context.resources.getString(R.string.siteSakinOnayı, resident.fullName)
        MaterialAlertDialogBuilder(context)
            .setMessage(text)
            .setPositiveButton(R.string.evet) { _, _ ->
                UserOperations.acceptResident(resident)
            }.setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }

    private fun showAlertMessageForRejectButton(resident: SiteResident) {
        val text = context.resources.getString(R.string.siteSakinReddi, resident.fullName)
        MaterialAlertDialogBuilder(context)
            .setMessage(text)
            .setPositiveButton(R.string.evet) { _, _ ->
                UserOperations.rejectResident(resident)
            }
            .setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
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
