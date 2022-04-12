package com.graduationproject.grad_project

import androidx.recyclerview.widget.DiffUtil
import com.graduationproject.grad_project.model.SiteResident

class ResidentsListItemCallback: DiffUtil.ItemCallback<SiteResident>() {
    override fun areItemsTheSame(oldItem: SiteResident, newItem: SiteResident): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: SiteResident, newItem: SiteResident): Boolean {
        return oldItem == newItem
    }
}
