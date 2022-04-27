package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.ContinuesVotingAdminAdapter
import com.graduationproject.grad_project.adapter.FinishedVotingAdminAdapter
import com.graduationproject.grad_project.databinding.FragmentVotingAdminBinding
import com.graduationproject.grad_project.viewmodel.VotingListAdminViewModel

class VotingAdminFragment : Fragment() {

    private var _binding: FragmentVotingAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VotingListAdminViewModel by viewModels()
    private lateinit var continuesAdapter: ContinuesVotingAdminAdapter
    private lateinit var finishedAdapter: FinishedVotingAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVotingAdminBinding.inflate(inflater, container, false)
        viewModel.continuesVoting.observe(viewLifecycleOwner) {
            continuesAdapter.submitList(it)
        }
        viewModel.finishedVoting.observe(viewLifecycleOwner) {
            finishedAdapter.submitList(it)
        }
        viewModel.retrieveFinishedVoting()
        viewModel.retrieveContinuesVoting()
        continuesAdapter = ContinuesVotingAdminAdapter()
        finishedAdapter = FinishedVotingAdminAdapter()
        binding.doneRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.continuesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.continuesRecyclerView.adapter = continuesAdapter
        binding.doneRecyclerView.adapter = finishedAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        fabSetOnClickListener()
        return binding.root
    }

    private fun fabSetOnClickListener() {
        binding.fab.setOnClickListener {
            val action = VotingAdminFragmentDirections.actionVotingAdminFragmentToAddVotingAdminFragment()
            requireView().findNavController().navigate(action)
        }
    }

}