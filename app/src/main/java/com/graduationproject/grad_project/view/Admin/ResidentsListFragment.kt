package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.ResidentsListAdapter
import com.graduationproject.grad_project.databinding.FragmentResidentsListBinding
import com.graduationproject.grad_project.view.admin.dialogs.AddingDebtDialogFragment
import com.graduationproject.grad_project.view.admin.dialogs.DeletingDebtDialogFragment
import com.graduationproject.grad_project.view.admin.dialogs.SendingMessageToResidentDialogFragment
import com.graduationproject.grad_project.viewmodel.ResidentsListViewModel

class ResidentsListFragment : Fragment() {

    private var _binding: FragmentResidentsListBinding? = null
    private val binding get() = _binding!!
    private var residentsListAdapter : ResidentsListAdapter? = null
    private val viewModel: ResidentsListViewModel by viewModels()
    private lateinit var popupMenu: PopupMenu
    private lateinit var anchor: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResidentsListBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel.residentsList.observe(viewLifecycleOwner) {
            residentsListAdapter?.submitList(it)
        }
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filter(newText)
                return true
            }
        })
        viewModel.getResidentsInASpecificSiteWithSnapshot()
        residentsListAdapter = ResidentsListAdapter { resident, anchorView ->
            viewModel.saveClickedResident(resident)
            anchor = anchorView
            createPopupMenu()
            popupMenuClickListener()
        }
        binding.recyclerview.adapter = residentsListAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    private fun createPopupMenu() {
        popupMenu = PopupMenu(requireContext(), anchor)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.resident_list_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun popupMenuClickListener() {
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_debt -> {
                    val addDebt = viewModel.clickedResident.value?.let { AddingDebtDialogFragment(it) }
                    addDebt?.show(parentFragmentManager, "addDebtDialog")
                    true
                }
                R.id.send_message -> {
                    val sendMessage = viewModel.clickedResident.value?.let {
                        SendingMessageToResidentDialogFragment(it)
                    }
                    sendMessage?.show(parentFragmentManager, "sendMessageDialog")
                    true
                }
                R.id.delete_debt -> {
                    val deleteDebt = viewModel.clickedResident.value?.let {
                        DeletingDebtDialogFragment(it)
                    }
                    deleteDebt?.show(parentFragmentManager, "deleteDebtDialog")
                    true
                }
                else -> false
            }
        }
    }
}