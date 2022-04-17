package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.ExpendituresItemBinding
import com.graduationproject.grad_project.model.Expenditure

class ExpendituresListAdapter
    : ListAdapter<Expenditure, ExpendituresListAdapter.ViewHolder>(ExpendituresDiffCallback()) {

    class ViewHolder private constructor(val binding: ExpendituresItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Expenditure) {
            binding.expenditure = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ExpendituresItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ExpendituresDiffCallback: DiffUtil.ItemCallback<Expenditure>() {
    override fun areItemsTheSame(oldItem: Expenditure, newItem: Expenditure): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Expenditure, newItem: Expenditure): Boolean {
        return oldItem == newItem
    }

}
