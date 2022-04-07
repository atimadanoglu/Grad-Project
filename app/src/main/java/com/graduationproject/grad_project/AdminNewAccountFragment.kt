package com.graduationproject.grad_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.components.SnackBars
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
        binding.goToAdminSiteInfoButton.setOnClickListener { goToSiteInformationButtonClicked() }
        binding.goToLoginPageButton.setOnClickListener { goBackToLoginFragment() }
        return view
    }

    private fun goBackToLoginFragment() {
        val action = AdminNewAccountFragmentDirections
            .actionAdminNewAccountFragmentToLoginFragment()
        findNavController().navigate(action)
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

    private fun goToSiteInformationButtonClicked() {
        if (!isEmpty()) {
            viewModel.setFullName(binding.fullNameText.text.toString())
            viewModel.setEmail(binding.TextEmailAddress.text.toString())
            viewModel.setPhoneNumber(binding.phoneNumberText.text.toString())
            viewModel.setPassword(binding.TextPassword.text.toString())
            viewModel.setActivationKey(binding.ActivationKey.text.toString())
            goToSiteInformationFragment()
            return
        }
        if (!isActivationKeyCorrect()) {
            view?.let { SnackBars.showWrongActivationCodeSnackBar(it) }
        }
        view?.let { SnackBars.showEmptySpacesSnackBar(it) }
    }

    private fun isEmpty(): Boolean {
        return binding.fullNameText.text.isEmpty() || binding.phoneNumberText.text.isEmpty()
                || binding.TextEmailAddress.text.isEmpty() || binding.TextPassword.text.isEmpty()
                || binding.ActivationKey.text.isEmpty()
    }

    private fun isActivationKeyCorrect(): Boolean = viewModel.activationKey == "qwerty"

    private fun goBackToSignUpMainFragment() {
        val action = AdminNewAccountFragmentDirections.actionAdminNewAccountFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }
}