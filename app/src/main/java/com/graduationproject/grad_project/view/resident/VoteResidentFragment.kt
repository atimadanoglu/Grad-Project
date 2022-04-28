package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.VoteResidentAdapter
import com.graduationproject.grad_project.databinding.FragmentVoteResidentBinding
import com.graduationproject.grad_project.viewmodel.VoteResidentViewModel

class VoteResidentFragment : Fragment() {

    private var _binding: FragmentVoteResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VoteResidentViewModel by viewModels()
    private lateinit var adapter: VoteResidentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentVoteResidentBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel.retrieveContinuesVoting()
        adapter = VoteResidentAdapter(parentFragmentManager)
        binding.continuesRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.continuesVoting.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        return view
    }

}