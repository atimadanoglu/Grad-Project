package com.graduationproject.grad_project

import androidx.recyclerview.widget.DiffUtil
import com.graduationproject.grad_project.model.SiteResident

class ResidentsListItemCallback(
    private val oldList: ArrayList<SiteResident>,
    private val newList: ArrayList<SiteResident>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].email == newList[newItemPosition].email
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}