package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.ExpendituresListResidentAdapter
import com.graduationproject.grad_project.databinding.FragmentSiteExpendituresResidentBinding
import com.graduationproject.grad_project.viewmodel.SiteExpendituresResidentViewModel

class SiteExpendituresResidentFragment : Fragment() {

    private var _binding: FragmentSiteExpendituresResidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ExpendituresListResidentAdapter
    private val viewModel: SiteExpendituresResidentViewModel by viewModels()
    private var menuView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSiteExpendituresResidentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.expenditures.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveExpenditures()
        adapter = ExpendituresListResidentAdapter { expenditure, view ->
            if (view != null) {
                menuView = view
            }
            viewModel.saveInfo(expenditure)
        }
        binding.siteExpenditureRecyclerview.adapter = adapter
        viewModel.openOptionsMenu.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                   chooseAction()
                }
            }
        }
        return binding.root
    }

    private fun goToShowExpenditureFragment(
        downloadUri: String, title: String, content: String
    ) {
        val action = SiteExpendituresResidentFragmentDirections
            .actionSiteExpendituresResidentFragmentToShowNotificationDialogFragment(downloadUri, title, content)
        findNavController().navigate(action)
    }

    private fun createPopUpMenu(): PopupMenu? {
        val popupMenu = menuView?.let { PopupMenu(requireContext(), it) }
        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.expenditures_menu, popupMenu.menu)
        popupMenu?.show()
        return popupMenu
    }

    private fun chooseAction() {
        val menu = createPopUpMenu()
        menu?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.showExpenditure -> {
                    goToShowExpenditureFragment(
                        viewModel.expenditure.value?.documentUri!!,
                        viewModel.expenditure.value?.title!!,
                        viewModel.expenditure.value?.content!!
                    )
                    true
                }
                R.id.deleteExpenditure -> {
                    viewModel.deleteExpenditure()
                    true
                }
                else -> false
            }
        }
    }

}