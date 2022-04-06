package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentAdminSiteInformationBinding
import com.graduationproject.grad_project.viewmodel.AdminSiteInformationViewModel

class AdminSiteInformationFragment : Fragment() {

    private var _binding: FragmentAdminSiteInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminSiteInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminSiteInformationBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backToAdminNewAccountFragmentButton.setOnClickListener { goBackToSignMainFragment() }
        binding.signUpButton.setOnClickListener { signUpButtonClicked() }
        return view
    }

    private fun signUpButtonClicked() {
        TODO("Not yet implemented")
    }

    private fun goBackToSignMainFragment() {
        val action = AdminSiteInformationFragmentDirections
            .actionAdminSiteInformationFragmentToAdminNewAccountFragment()
        findNavController().navigate(action)
    }

}