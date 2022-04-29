package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.PendingApprovalAdapter
import com.graduationproject.grad_project.databinding.FragmentPendingApprovalsAdminBinding
import com.graduationproject.grad_project.viewmodel.PendingApprovalsViewModel

class PendingApprovalsAdminFragment : Fragment() {

    private var _binding: FragmentPendingApprovalsAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PendingApprovalAdapter
    private val viewModel: PendingApprovalsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPendingApprovalsAdminBinding.inflate(inflater, container, false)
        adapter = PendingApprovalAdapter(requireContext())
        binding.awaitingResidentsRecyclerView.adapter = adapter
        viewModel.awaitingResidents.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveAwaitingResidents()

        return binding.root
    }

}