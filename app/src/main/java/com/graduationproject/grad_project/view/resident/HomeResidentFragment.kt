package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentHomeResidentBinding
import com.graduationproject.grad_project.viewmodel.HomeResidentViewModel

class HomeResidentFragment : Fragment() {

    private var _binding: FragmentHomeResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeResidentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.retrieveMyDebtInfo()
        viewModel.retrieveMyNotificationsCount()
        viewModel.retrieveMyRequestsCount()
        binding.request.setOnClickListener { goToRequestsPage() }
        binding.notificationsResidentHome.setOnClickListener { goToNotificationsPage() }
        binding.myDebt.setOnClickListener { goToPayDebtPage() }
        return binding.root
    }

    private fun goToPayDebtPage() {
        val action = HomeResidentFragmentDirections.actionHomeResidentFragmentToPayDebtFragment()
        findNavController().navigate(action)
    }

    private fun goToNotificationsPage() {
        val action = HomeResidentFragmentDirections.actionHomeResidentFragmentToNotificationResidentFragment()
        findNavController().navigate(action)
    }

    private fun goToRequestsPage() {
        val action = HomeResidentFragmentDirections.actionHomeResidentFragmentToRequestsFragment()
        findNavController().navigate(action)
    }
}