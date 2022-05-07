package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.net.Uri
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
        adapter = PendingApprovalAdapter(requireContext()) { phoneNumber ->
            viewModel.navigateToPhoneDial(phoneNumber)
        }
        observeViewModelProperties()
        binding.awaitingResidentsRecyclerView.adapter = adapter
        viewModel.retrieveAwaitingResidents()
        return binding.root
    }

    private fun observeViewModelProperties() {
        viewModel.awaitingResidents.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.navigateToPhoneDial.observe(viewLifecycleOwner) {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${viewModel.phoneNumber}"))
            startActivity(callIntent)
        }
    }

}