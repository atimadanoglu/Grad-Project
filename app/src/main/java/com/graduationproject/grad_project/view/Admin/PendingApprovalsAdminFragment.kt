package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
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
        adapter = PendingApprovalAdapter({
            viewModel.navigateToPhoneDial(it)
        },{
            viewModel.saveClickedResident(it)
            showAlertMessageForAcceptButton()
        },{
            viewModel.saveClickedResident(it)
            showAlertMessageForRejectButton()
        })
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
            it?.let {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${viewModel.phoneNumber}"))
                startActivity(callIntent)
                viewModel.navigatedToPhoneDial()
            }
        }
    }

    private fun showAlertMessageForAcceptButton() {
        val text = resources.getString(R.string.siteSakinOnayı, viewModel.clickedResident.value?.fullName)
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(text)
            .setPositiveButton(R.string.evet) { _, _ ->
                viewModel.acceptResident()
            }.setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }

    private fun showAlertMessageForRejectButton() {
        val text = resources.getString(R.string.siteSakinReddi, viewModel.clickedResident.value?.fullName)
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(text)
            .setPositiveButton(R.string.evet) { _, _ ->
                viewModel.rejectResident()
            }
            .setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }
}