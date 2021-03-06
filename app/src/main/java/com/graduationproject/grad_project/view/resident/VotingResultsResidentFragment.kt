package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.adapter.VotingResultsResidentAdapter
import com.graduationproject.grad_project.databinding.FragmentVotingResultsResidentBinding
import com.graduationproject.grad_project.viewmodel.VotingResultsResidentViewModel

class VotingResultsResidentFragment : Fragment() {

    private var _binding: FragmentVotingResultsResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VotingResultsResidentViewModel by viewModels()
    private lateinit var adapter: VotingResultsResidentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVotingResultsResidentBinding.inflate(inflater, container, false)
        viewModel.voting.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveVoting()
        adapter = VotingResultsResidentAdapter()
        binding.doneRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}