package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ExpendituresItemBinding
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure
import kotlinx.coroutines.*

class ExpendituresListAdapter(private val context: Context?)
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
        holder.binding.expendituresMoreIconButton.setOnClickListener {
            val popupMenu = createPopUpMenu(it)
            popupMenu?.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.deleteExpenditure -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val email = async {
                                FirebaseAuth.getInstance().currentUser?.email
                            }

                            email.await()?.let { adminEmail ->
                                ExpendituresOperations.deleteExpenditure(adminEmail, item)
                            }.also {
                                withContext(Dispatchers.Main) {
                                    val newList = arrayListOf<Expenditure>()
                                    newList.addAll(currentList)
                                    newList.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position - 1, newList.size)
                                    submitList(newList)
                                }
                            }
                        }
                        true
                    }
                    else -> false
                }
            }

        }
    }

    private fun createPopUpMenu(view: View): PopupMenu? {
        val popupMenu = context?.let { PopupMenu(it, view) }
        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.expenditures_menu, popupMenu.menu)
        popupMenu?.show()
        return popupMenu
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
