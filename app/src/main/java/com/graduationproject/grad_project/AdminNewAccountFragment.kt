package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentAdminNewAccountBinding
import com.graduationproject.grad_project.viewmodel.AdminNewAccountViewModel

class AdminNewAccountFragment : Fragment() {

    private var _binding: FragmentAdminNewAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminNewAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminNewAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backToSignUpMainFragmentButton.setOnClickListener { goBackToSignUpMainFragment() }
        binding.goToAdminSiteInfoButton.setOnClickListener { goToSiteInformationFragment() }
        return view
    }

    private fun goToSiteInformationFragment() {
        val action = AdminNewAccountFragmentDirections
            .actionAdminNewAccountFragmentToAdminSiteInformationFragment(
                viewModel.fullName,
                viewModel.phoneNumber,
                viewModel.email,
                viewModel.password
            )
        findNavController().navigate(action)
    }

    private fun goBackToSignUpMainFragment() {
        val action = AdminNewAccountFragmentDirections.actionAdminNewAccountFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }
}