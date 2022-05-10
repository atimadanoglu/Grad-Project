package com.graduationproject.grad_project.view.resident

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.adapter.ServicesResidentAdapter
import com.graduationproject.grad_project.databinding.FragmentServicesResidentBinding
import com.graduationproject.grad_project.viewmodel.ServicesResidentViewModel

class ServicesResidentFragment : Fragment() {

    private var _binding: FragmentServicesResidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ServicesResidentAdapter
    private val viewModel: ServicesResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServicesResidentBinding.inflate(inflater, container, false)
        viewModel.retrieveServices()
        observeViewModelProperties()
        adapter = ServicesResidentAdapter { phoneNumber ->
            viewModel.navigateToPhoneDial(phoneNumber)
        }
        binding.servicesRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun observeViewModelProperties() {
        viewModel.services.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.navigateToPhoneDial.observe(viewLifecycleOwner) {
            it?.let {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${viewModel.phoneNumber}"))
                requireActivity().startActivity(callIntent)
                viewModel.navigatedPhoneDial()
            }
        }
    }
}