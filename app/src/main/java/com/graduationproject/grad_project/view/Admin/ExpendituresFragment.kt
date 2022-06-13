package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.ExpendituresListAdapter
import com.graduationproject.grad_project.databinding.FragmentExpendituresBinding
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.view.admin.dialogs.ShowExpenditureFragment
import com.graduationproject.grad_project.viewmodel.ExpendituresViewModel

class ExpendituresFragment : Fragment() {

    private var _binding: FragmentExpendituresBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpendituresViewModel by viewModels()
    private lateinit var adapter: ExpendituresListAdapter
    private lateinit var popupMenu: PopupMenu
    // Anchor view for popUpMenu
    private lateinit var anchor: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExpendituresBinding.inflate(inflater, container, false)
        viewModel.expenditures.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveAllExpenditures()
        adapter = ExpendituresListAdapter { expenditure, view ->
            viewModel.saveExpenditure(expenditure)
            anchor = view
            createPopUpMenu(anchor)
            popUpMenuItemClickListener()
        }
        binding.expendituresRecyclerview.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addExpendituresButton.setOnClickListener {
            goToAddExpendituresFragmentButton()
        }
        return binding.root
    }

    private fun goToAddExpendituresFragmentButton() {
        val action = ExpendituresFragmentDirections.actionExpendituresFragmentToAddExpendituresFragment()
        findNavController().navigate(action)
    }

    private fun createPopUpMenu(view: View) {
        popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.expenditures_menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun popUpMenuItemClickListener() {
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.showExpenditure -> {
                    val dialog = viewModel.expenditure.value?.let { ShowExpenditureFragment(it) }
                    dialog?.show(parentFragmentManager, "expenditureDialog")
                    true
                }
                R.id.deleteExpenditure -> {
                    viewModel.expenditure.value?.let { ExpendituresOperations.deleteExpenditure(it) }
                    true
                }
                else -> false
            }
        }
    }
}