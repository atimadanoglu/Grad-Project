package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.components.SnackBars
import com.graduationproject.grad_project.databinding.FragmentResidentNewAccountBinding
import com.graduationproject.grad_project.viewmodel.ResidentNewAccountViewModel

class ResidentNewAccountFragment : Fragment() {

    private var _binding: FragmentResidentNewAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResidentNewAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResidentNewAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.backToSignUpMainFragmentButton.setOnClickListener { goBackToSignUpMainFragment() }
        binding.goToResidentSiteInfoButton.setOnClickListener { goToResidentSiteInfoButtonClicked() }
        binding.goToLoginPageButton.setOnClickListener { goBackToLoginFragment() }
        return view
    }

    private fun goBackToLoginFragment() {
        val action = ResidentNewAccountFragmentDirections
            .actionResidentNewAccountFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun updateViewModelData() {
        viewModel.setFullName(binding.fullNameText.text.toString())
        viewModel.setEmail(binding.TextEmailAddress.text.toString())
        viewModel.setPhoneNumber(binding.phoneNumberText.text.toString())
        viewModel.setPassword(binding.TextPassword.text.toString())
    }

    private fun goToResidentSiteInfoButtonClicked() {
        updateViewModelData()
        if (!isBlank()) {
            goToResidentSiteInfoFragment()
            return
        }
        SnackBars.showEmptySpacesSnackBar(view)
    }

    private fun isBlank(): Boolean {
        return viewModel.email.isBlank() || viewModel.fullName.isBlank()
                || viewModel.phoneNumber.isBlank() || viewModel.password.isBlank()
    }

    private fun goToResidentSiteInfoFragment() {
        val action = ResidentNewAccountFragmentDirections
            .actionResidentNewAccountFragmentToResidentSiteInformationFragment(
                viewModel.fullName,
                viewModel.phoneNumber,
                viewModel.email,
                viewModel.password
            )
        findNavController().navigate(action)
    }

    private fun goBackToSignUpMainFragment() {
        val action = ResidentNewAccountFragmentDirections.actionResidentNewAccountFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }

}