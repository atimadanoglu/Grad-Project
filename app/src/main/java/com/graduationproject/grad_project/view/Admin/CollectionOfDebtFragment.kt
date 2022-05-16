package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.adapter.CollectionOfPaymentsAdapter
import com.graduationproject.grad_project.databinding.FragmentCollectionOfDebtBinding
import com.graduationproject.grad_project.viewmodel.CollectionOfPaymentsViewModel


class CollectionOfDebtFragment : Fragment() {

    private var _binding: FragmentCollectionOfDebtBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CollectionOfPaymentsAdapter
    private val viewModel: CollectionOfPaymentsViewModel by viewModels()
    private var paymentOptionsMenu: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCollectionOfDebtBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = CollectionOfPaymentsAdapter { payment, view ->
            paymentOptionsMenu = view
            viewModel.savePaymentInfo(payment)
        }
        binding.collectionOfDebtRecyclerview.adapter = adapter
        viewModel.retrievePayments()
        viewModel.payments.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
        /*viewModel.openMenuOptions.observe(viewLifecycleOwner) {
            it?.let {
                chooseAction()
            }
        }*/
        return binding.root
    }
/*
    private fun createPopUpMenu(): PopupMenu? {
        val popupMenu = paymentOptionsMenu?.let { PopupMenu(requireContext(), it) }
        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu?.show()
        return popupMenu
    }

    private fun goToCollectionOfPaymentsPage() {
        val action = CollectionOfDebtFragmentDirections.actionCollectionOfDebtFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun chooseAction() {
        val menu = createPopUpMenu()
        menu?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.announcementInfo -> {
                    goToCollectionOfPaymentsPage()
                    true
                }
                else -> false
            }
        }
    }*/
}