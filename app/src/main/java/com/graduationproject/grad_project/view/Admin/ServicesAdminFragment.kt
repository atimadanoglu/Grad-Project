package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.net.Uri
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
    private var callIntent: Intent? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServicesAdminBinding.inflate(inflater, container, false)
        val view = binding.root
        adapter = ServicesAdminAdapter { phoneNumber ->  
            viewModel.navigateToPhoneDial(phoneNumber)
        }
        viewModel.retrieveServices()
        viewModel.services.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.navigateToPhoneDial.observe(viewLifecycleOwner) {
            it?.let {
                callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${viewModel.phoneNumber}"))
                requireActivity().startActivity(callIntent)
                viewModel.navigatedPhoneDial()
            }
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