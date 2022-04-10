package com.graduationproject.grad_project

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.ResidentsListAdapter.*
import com.graduationproject.grad_project.databinding.ListItemBinding
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.view.admin.dialogs.AddingDebtDialogFragment
import com.graduationproject.grad_project.view.admin.dialogs.DeletingDebtDialogFragment
import com.graduationproject.grad_project.view.admin.dialogs.SendingMessageToResidentDialogFragment
import kotlinx.coroutines.*

class ResidentsListAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
) :ListAdapter<SiteResident, ResidentViewHolder>(ResidentsListItemCallback()) {

    class ResidentViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val accountName = binding.accountNameTextListItem
        private val block = binding.blockNoTextListItem
        private val flatNo = binding.flatNoAddDebt
        private val debtText = binding.debtAmount
        val menu = binding.moreIconButton
        companion object {
            fun inflateFrom(parent: ViewGroup): ResidentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ResidentViewHolder(binding)
            }
        }

        fun bind(resident: SiteResident) {
            accountName.text = resident.fullName
            val blockText = "Blok : " + resident.blockNo
            block.text = blockText
            val flat = "Daire : " + resident.flatNo.toString()
            flatNo.text = flat
            val debt =  resident.debt.toString() + " TL"
            debtText.text = debt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder
    = ResidentViewHolder.inflateFrom(parent)


    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        val resident = getItem(position)
        holder.bind(resident)
        holder.menu.setOnClickListener { view ->
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