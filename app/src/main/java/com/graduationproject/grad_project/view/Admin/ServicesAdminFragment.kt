package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.graduationproject.grad_project.adapter.ServicesAdminAdapter
import com.graduationproject.grad_project.databinding.FragmentServicesAdminBinding
import com.graduationproject.grad_project.viewmodel.ServicesAdminViewModel

class ServicesAdminFragment : Fragment() {

    private var _binding: FragmentServicesAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesAdminViewModel by viewModels()
    private lateinit var adapter: ServicesAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServicesAdminBinding.inflate(inflater, container, false)
        val view = binding.root
        adapter = ServicesAdminAdapter()
        viewModel.retrieveServices()
        viewModel.services.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        binding.servicesRecyclerView.setHasFixedSize(true)
        binding.servicesRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.servicesAdminFloatingActionButton.setOnClickListener {
            val action = ServicesAdminFragmentDirections.actionServicesAdminFragmentToServicesAdminDialogFragment()
            requireView().findNavController().navigate(action)
        }

        return view
    }

}