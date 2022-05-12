package com.graduationproject.grad_project.adapter

import android.graphics.Color
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

            private const val RED = "#8f170e"
            private const val GREEN = "#284443"
            private const val SUGGESTION = "Öneri"
            private const val COMPLAINT = "Şikayet"

            fun inflateFrom(parent: ViewGroup): RequestViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestsItemBinding.inflate(layoutInflater, parent, false)
                return RequestViewHolder(binding)
            }
        }
        fun bind(request: Request, clickListener: (request: Request, view: View) -> Unit) {
            binding.request = request
            setColor(request)
            binding.requestOptions.setOnClickListener {
                clickListener(request, it)
            }
            binding.executePendingBindings()
        }
        private fun setColor(request: Request) {
            if (request.type == SUGGESTION) {
                binding.itemType.setBackgroundColor(Color.parseColor(GREEN))
                binding.itemType.setTextColor(Color.WHITE)
            }
            if (request.type == COMPLAINT) {
                binding.itemType.setBackgroundColor(Color.parseColor(RED))
                binding.itemType.setTextColor(Color.WHITE)
            }
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
