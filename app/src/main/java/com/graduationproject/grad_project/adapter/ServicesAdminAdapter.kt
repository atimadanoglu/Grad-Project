package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ServicesRowBinding
import com.graduationproject.grad_project.model.Service

class ServicesAdminAdapter: ListAdapter<Service, ServicesAdminAdapter.ServiceViewHolder>(ServiceAdminDiffUtil()) {

    class ServiceViewHolder(val binding: ServicesRowBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): ServiceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ServicesRowBinding.inflate(layoutInflater, parent, false)
                return ServiceViewHolder(binding)
            }
        }
        fun bind(service: Service) {
            putImage(service)
            binding.service = service
            binding.executePendingBindings()
        }

        private fun putImage(service: Service) {
            val resourceId = when(service.type) {
                "Taksi" -> R.drawable.taxi_icon
                "Elektrikçi" -> R.drawable.electric_icon
                "Tesisatçı" -> R.drawable.plumber_icon
                "Çilingir" -> R.drawable.password_icon
                else -> 0
            }
            if (resourceId != 0) {
                binding.serviceIcons.setImageResource(resourceId)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        return ServiceViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ServiceAdminDiffUtil: DiffUtil.ItemCallback<Service>() {
    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }
}
