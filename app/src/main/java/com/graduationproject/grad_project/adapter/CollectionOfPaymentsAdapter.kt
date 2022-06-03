package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.databinding.PaymentListItemBinding
import com.graduationproject.grad_project.model.Payment

class CollectionOfPaymentsAdapter: ListAdapter<Payment, CollectionOfPaymentsAdapter.PaymentViewHolder>(PaymentDiffUtil()) {
    class PaymentViewHolder(private val binding: PaymentListItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): PaymentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PaymentListItemBinding.inflate(layoutInflater, parent, false)
                return PaymentViewHolder(binding)
            }
        }
        fun bind(payment: Payment) {
            binding.payment = payment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class PaymentDiffUtil: DiffUtil.ItemCallback<Payment>() {
    override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
        return oldItem == newItem
    }
}