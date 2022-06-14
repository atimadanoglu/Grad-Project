package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.adapter.ResidentsListAdapter.ResidentViewHolder
import com.graduationproject.grad_project.databinding.ListItemBinding
import com.graduationproject.grad_project.model.SiteResident

class ResidentsListAdapter(
    private val clickListener: (resident: SiteResident, anchorView: View) -> Unit
) :ListAdapter<SiteResident, ResidentViewHolder>(ResidentsListItemCallback()) {

    class ResidentViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): ResidentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ResidentViewHolder(binding)
            }
        }

        fun bind(resident: SiteResident, clickListener: (resident: SiteResident, anchorView: View) -> Unit) {
            binding.resident = resident
            binding.moreIconButton.setOnClickListener {
                clickListener(resident, it)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder
            = ResidentViewHolder.inflateFrom(parent)


    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        val resident = getItem(position)
        holder.bind(resident, clickListener)
    }
}

class ResidentsListItemCallback: DiffUtil.ItemCallback<SiteResident>() {
    override fun areItemsTheSame(oldItem: SiteResident, newItem: SiteResident): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: SiteResident, newItem: SiteResident): Boolean {
        return oldItem == newItem
    }
}
