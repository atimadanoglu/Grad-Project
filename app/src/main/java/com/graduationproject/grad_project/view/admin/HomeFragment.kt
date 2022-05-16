package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentHomeBinding
import com.graduationproject.grad_project.viewmodel.HomeFragmentViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.request.setOnClickListener { goToRequestsPage() }
        binding.votingCardView.setOnClickListener { goToVotingPage() }
        binding.collectionOfDebt.setOnClickListener { goToPaymentsListPage() }
        viewModel.retrieveTotalCollectedMoney()
        viewModel.retrieveRequestsCount()
        viewModel.retrieveTotalVotingMoney()
        return binding.root
    }

    private fun goToRequestsPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToRequestAdminFragment()
        findNavController().navigate(action)
    }

    private fun goToVotingPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToVotingAdminFragment()
        findNavController().navigate(action)
    }

    private fun goToPaymentsListPage() {
        val action = HomeFragmentDirections.actionHomeFragmentToCollectionOfDebtFragment()
        findNavController().navigate(action)
    }
}