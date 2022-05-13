package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.request.setOnClickListener { goToRequestsPage() }
        binding.votingCardView.setOnClickListener { goToVotingPage() }
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
}