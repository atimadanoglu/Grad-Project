package com.graduationproject.grad_project.view.resident

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        emailAddressTextFocusChangeListener()
        phoneNumberTextFocusChangeListener()
        passwordTextChangeListener()
        setOnClickListeners()
        return view
    }

    private fun setOnClickListeners() {
        binding.goToLoginPageButton.setOnClickListener { goBackToLoginFragment() }
        binding.goToResidentSiteInfoButton.setOnClickListener {
            if (viewModel.allAreValid()) {
                goToResidentSiteInfoButtonClicked()
            }
        }
        binding.backToSignUpMainFragmentButton.setOnClickListener { goBackToSignUpMainFragment() }
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
        val action = ResidentNewAccountFragmentDirections
            .actionResidentNewAccountFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun goToResidentSiteInfoButtonClicked() {
        if (!viewModel.isEmpty()) {
            goToResidentSiteInfoFragment()
            return
        }
        SnackBars.showEmptySpacesSnackBar(view)
    }

    private fun goToResidentSiteInfoFragment() {
        if (!viewModel.isEmpty()) {
            val action = ResidentNewAccountFragmentDirections
                .actionResidentNewAccountFragmentToResidentSiteInformationFragment(
                    viewModel.fullName.value!!,
                    viewModel.phoneNumber.value!!,
                    viewModel.email.value!!,
                    viewModel.password.value!!
                )
            findNavController().navigate(action)
        }
    }

    private fun goBackToSignUpMainFragment() {
        val action = ResidentNewAccountFragmentDirections.actionResidentNewAccountFragmentToSignUpMainFragment()
        findNavController().navigate(action)
    }
}