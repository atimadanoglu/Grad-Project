package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
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
        viewModel.getAdmin()
        viewModel.fullName.observe(viewLifecycleOwner) {
            binding.nameText.text = it
        }
        viewModel.email.observe(viewLifecycleOwner) {
            binding.emailText.text = it
        }
        viewModel.phoneNumber.observe(viewLifecycleOwner) {
            binding.phoneText.text = it
        }
        binding.cardViewName.setOnClickListener { goToUpdateNamePage() }
        binding.cardViewEmail.setOnClickListener { goToUpdateEmailPage() }
        binding.cardViewPhone.setOnClickListener { goToUpdatePhoneNumberPage() }
        binding.carViewPassword.setOnClickListener { goToUpdatePasswordPage() }
        return binding.root
    }

    private fun goToUpdatePasswordPage() {
        val action = SettingsAdminFragmentDirections.actionSettingsAdminFragmentToSettingsPasswordFragment()
        requireView().findNavController().navigate(action)
    }

    private fun goToUpdatePhoneNumberPage() {
        val action = SettingsAdminFragmentDirections.actionSettingsAdminFragmentToSettingsPhoneFragment()
        requireView().findNavController().navigate(action)
    }

    private fun goToUpdateEmailPage() {
        val action = SettingsAdminFragmentDirections.actionSettingsAdminFragmentToSettingsEmailFragment()
        requireView().findNavController().navigate(action)
    }

    private fun goToUpdateNamePage() {
        val action = SettingsAdminFragmentDirections.actionSettingsAdminFragmentToSettingsNameFragment()
        requireView().findNavController().navigate(action)
    }

}