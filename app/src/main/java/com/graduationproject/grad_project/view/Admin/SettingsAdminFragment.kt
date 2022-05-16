package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.grad_project.databinding.FragmentSettingsAdminBinding
import com.graduationproject.grad_project.viewmodel.SettingsAdminViewModel


class SettingsAdminFragment : Fragment() {

    private var _binding: FragmentSettingsAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsAdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsAdminBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getAdmin()
        viewModel.fullName.observe(viewLifecycleOwner) {
            it?.let {
                binding.nameText.text = it
            }
        }

        viewModel.phoneNumber.observe(viewLifecycleOwner) {
            it?.let {
                binding.phoneText.text = it
            }
        }
        binding.cardViewName.setOnClickListener { goToUpdateNamePage() }
        binding.cardViewPhone.setOnClickListener { goToUpdatePhoneNumberPage() }
        binding.carViewPassword.setOnClickListener { goToUpdatePasswordPage() }
        return binding.root
    }

    private fun goToUpdatePhoneNumberPage() {
        val action = viewModel.phoneNumber.value?.let {
            SettingsAdminFragmentDirections
                .actionSettingsAdminFragmentToSettingsPhoneFragment(it)
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    private fun goToUpdateNamePage() {
        val action = viewModel.fullName.value?.let {
            SettingsAdminFragmentDirections
                .actionSettingsAdminFragmentToSettingsNameFragment(it)
        }
        if (action != null) {
            requireView().findNavController().navigate(action)
        }
    }
    private fun goToUpdatePasswordPage() {
        val action = SettingsAdminFragmentDirections.actionSettingsAdminFragmentToSettingsPasswordFragment2()
        findNavController().navigate(action)
    }
}