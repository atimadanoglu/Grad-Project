package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.R
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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        emailAddressTextFocusChangeListener()
        phoneNumberTextFocusChangeListener()
        passwordTextChangeListener()
        setOnClickListeners()
        return view
    }

    private fun setOnClickListeners() {
        binding.backToSignUpMainFragmentButton.setOnClickListener { goBackToSignUpMainFragment() }
        binding.goToAdminSiteInfoButton.setOnClickListener {
            if (viewModel.allAreValid()) {
                goToSiteInformationButtonClicked()
            }
        }
        binding.goToLoginPageButton.setOnClickListener { goBackToLoginFragment() }
    }

    private fun emailAddressTextFocusChangeListener() {
        binding.email.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (!viewModel.isValidEmail()) {
                    val errorMessage = resources.getString(R.string.geçersizEmailAdresi)
                    binding.emailLayout.error = errorMessage
                } else {
                    binding.emailLayout.error = null
                }
            }
        }
    }

    private fun phoneNumberTextFocusChangeListener() {
        binding.phoneNumber.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (!viewModel.isValidPhoneNumber()) {
                    val errorMessage = resources.getString(R.string.geçersizTelefonNo)
                    binding.phoneNumberLayout.error = errorMessage
                } else {
                    binding.phoneNumberLayout.error = null
                }
            }
        }
    }

    private fun passwordTextChangeListener() {
        binding.password.addTextChangedListener {
            if (!viewModel.isValidPassword()) {
                val errorMessage = resources.getString(R.string.şifreBilgi)
                binding.passwordLayout.error = errorMessage
            } else {
                binding.passwordLayout.error = null
            }
        }
    }

    private fun goBackToLoginFragment() {
        val action = AdminNewAccountFragmentDirections
            .actionAdminNewAccountFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun goToSiteInformationFragment() {
        val action = AdminNewAccountFragmentDirections
            .actionAdminNewAccountFragmentToAdminSiteInformationFragment(
                viewModel.fullName.value!!,
                viewModel.phoneNumber.value!!,
                viewModel.email.value!!,
                viewModel.password.value!!
            )
        findNavController().navigate(action)
    }

    private fun goToSiteInformationButtonClicked() {
        if (!viewModel.isEmpty()) {
            if (viewModel.isActivationKeyCorrect()) {
                goToSiteInformationFragment()
                return
            } else {
                SnackBars.showWrongActivationCodeSnackBar(view)
            }
        } else {
            SnackBars.showEmptySpacesSnackBar(view)
        }
    }

    private fun goBackToSignUpMainFragment() {
        val action = AdminNewAccountFragmentDirections.actionAdminNewAccountFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }
}