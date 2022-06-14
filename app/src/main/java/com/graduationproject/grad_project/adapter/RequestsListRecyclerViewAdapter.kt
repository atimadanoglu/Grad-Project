package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.RequestsItemBinding
import com.graduationproject.grad_project.model.Request

class RequestsListRecyclerViewAdapter(
    private val clickListener: (request: Request, anchor: View) -> Unit
): ListAdapter<Request, RequestsListRecyclerViewAdapter.RequestViewHolder>(RequestsDiff()) {

    companion object {
        const val TAG = "RequestsListRecyclerViewAdapter"
    }
    class RequestViewHolder(val binding: RequestsItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): RequestViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestsItemBinding.inflate(layoutInflater, parent, false)
                return RequestViewHolder(binding)
            }
        }

        fun bind(request: Request, clickListener: (request: Request, anchor: View) -> Unit) {
            binding.request = request
            binding.requestOptions.setOnClickListener {
                clickListener(request, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): RequestViewHolder = RequestViewHolder.inflateFrom(parent)


    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        holder.bind(request, clickListener)
    }
}

class RequestsDiff: DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}
