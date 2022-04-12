package com.graduationproject.grad_project

import androidx.recyclerview.widget.DiffUtil
import com.graduationproject.grad_project.model.Request

class RequestsDiff: DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}