package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ExpendituresItemBinding
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure
import com.graduationproject.grad_project.view.admin.dialogs.ShowExpenditureFragment

class ExpendituresListAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context?,
)
    : ListAdapter<Expenditure, ExpendituresListAdapter.ViewHolder>(ExpendituresDiffCallback()) {

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
        fun bind(item: Expenditure) {
            binding.expenditure = item
            binding.executePendingBindings()
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
                    R.id.showExpenditure -> {
                        val dialog = ShowExpenditureFragment(item)
                        dialog.show(fragmentManager, "expenditureDialog")
                        true
                    }
                    R.id.deleteExpenditure -> {
                        ExpendituresOperations.deleteExpenditure(item)
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
