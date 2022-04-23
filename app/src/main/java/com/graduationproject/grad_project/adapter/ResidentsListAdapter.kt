package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.ResidentsListAdapter.ResidentViewHolder
import com.graduationproject.grad_project.databinding.ListItemBinding
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.view.admin.dialogs.AddingDebtDialogFragment
import com.graduationproject.grad_project.view.admin.dialogs.DeletingDebtDialogFragment
import com.graduationproject.grad_project.view.admin.dialogs.SendingMessageToResidentDialogFragment

class ResidentsListAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
) :ListAdapter<SiteResident, ResidentViewHolder>(ResidentsListItemCallback()) {

    class ResidentViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): ResidentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ResidentViewHolder(binding)
            }
        }

        fun bind(resident: SiteResident) {
            binding.resident = resident
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder
            = ResidentViewHolder.inflateFrom(parent)


    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        val resident = getItem(position)
        holder.bind(resident)
        holder.binding.moreIconButton.setOnClickListener { view ->
            val popup: PopupMenu = createPopUpMenu(view)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.add_debt -> {
                        val addDebt = resident?.let { AddingDebtDialogFragment(it) }
                        addDebt?.show(fragmentManager, "addDebtDialog")
                        true
                    }
                    R.id.send_message -> {
                        val sendMessage = resident?.let { SendingMessageToResidentDialogFragment(it) }
                        sendMessage?.show(fragmentManager, "sendMessageDialog")
                        true
                    }
                    R.id.delete_debt -> {
                        val deleteDebt = resident?.let { DeletingDebtDialogFragment(it) }
                        deleteDebt?.show(fragmentManager, "deleteDebtDialog")
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun createPopUpMenu(view: View): PopupMenu {
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.resident_list_menu, popup.menu)
        popup.show()
        return popup
    }

    /*fun filterList(list: ArrayList<SiteResident?>) {
        siteResidents = list
        submitList(list)
    }

   fun updateResidentsList(newResidents: ArrayList<SiteResident?>) {
       siteResidents.clear()
       siteResidents.addAll(newResidents)
       submitList(siteResidents)
   }*/

}