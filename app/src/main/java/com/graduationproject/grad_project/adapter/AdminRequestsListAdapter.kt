package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.RequestsItemBinding
import com.graduationproject.grad_project.model.Request

class AdminRequestsListAdapter(
    private val clickListener: (request: Request, view: View) -> Unit
): ListAdapter<Request, AdminRequestsListAdapter.RequestViewHolder>(RequestDiffUtil()) {
    class RequestViewHolder(val binding: RequestsItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): RequestViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestsItemBinding.inflate(layoutInflater, parent, false)
                return RequestViewHolder(binding)
            }
        }
        fun bind(request: Request, clickListener: (request: Request, view: View) -> Unit) {
            binding.request = request
            binding.requestOptions.setOnClickListener {
                clickListener(request, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class RequestDiffUtil: DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}
