package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.ExpendituresItemBinding
import com.graduationproject.grad_project.model.Expenditure

class ExpendituresListResidentAdapter:
    ListAdapter<Expenditure, ExpendituresListResidentAdapter.ExpenditureViewHolder>(ExpendituresResidentDiffUtil()) {
    class ExpenditureViewHolder(val binding: ExpendituresItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): ExpenditureViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ExpendituresItemBinding.inflate(layoutInflater, parent, false)
                return ExpenditureViewHolder(binding)
            }
        }
        fun bind(expenditure: Expenditure) {
            binding.expenditure = expenditure
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenditureViewHolder {
        return ExpenditureViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: ExpenditureViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ExpendituresResidentDiffUtil: DiffUtil.ItemCallback<Expenditure>() {
    override fun areItemsTheSame(oldItem: Expenditure, newItem: Expenditure): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Expenditure, newItem: Expenditure): Boolean {
        return oldItem == newItem
    }
}
