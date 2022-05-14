package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentSettingsResidentBinding
import com.graduationproject.grad_project.viewmodel.SettingsResidentViewModel

class SettingsResidentFragment : Fragment() {
    private var _binding: FragmentSettingsResidentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsResidentBinding.inflate(inflater, container, false)
        viewModel.getResident()
        viewModel.fullName.observe(viewLifecycleOwner) {
            binding.nameText.text = it
        }

        viewModel.phoneNumber.observe(viewLifecycleOwner) {
            binding.phoneText.text = it
        }
        binding.cardViewName.setOnClickListener { goToUpdateNamePage() }
        binding.cardViewPhone.setOnClickListener { goToUpdatePhoneNumberPage() }
        binding.carViewPassword.setOnClickListener { goToUpdatePasswordPage() }
        return binding.root
    }

    private fun goToUpdatePhoneNumberPage() {
        val action = SettingsResidentFragmentDirections.actionSettingsResidentFragmentToSettingsResidentPhoneFragment()
        requireView().findNavController().navigate(action)
    }

    private fun goToUpdateNamePage() {
        val action = viewModel.fullName.value?.let {
            SettingsResidentFragmentDirections.actionSettingsResidentFragmentToSettingsResidentNameFragment(
                it
            )
        }
        if (action != null) {
            requireView().findNavController().navigate(action)
        }
    }
    private fun goToUpdatePasswordPage() {
        val action = SettingsResidentFragmentDirections.actionSettingsResidentFragmentToSettingsResidentPasswordFragment()
        findNavController().navigate(action)
    }
}