package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.ExpendituresItemBinding
import com.graduationproject.grad_project.model.Expenditure

class ExpendituresListAdapter(
    private val clickListener: (expenditure: Expenditure, view: View) -> Unit
) : ListAdapter<Expenditure, ExpendituresListAdapter.ViewHolder>(ExpendituresDiffCallback()) {

    companion object {
        const val TAG = "ExpendituresListAdapter"
    }

    class ViewHolder private constructor(val binding: ExpendituresItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ExpendituresItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
        fun bind(item: Expenditure, clickListener: (expenditure: Expenditure, view: View) -> Unit) {
            binding.expenditure = item
            binding.expendituresMoreIconButton.setOnClickListener {
                clickListener(item, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
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
