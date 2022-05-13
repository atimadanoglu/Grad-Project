package com.graduationproject.grad_project.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentSettingsPasswordBinding
import com.graduationproject.grad_project.viewmodel.SettingsPasswordViewModel

class SettingsPasswordFragment : Fragment() {

    private var _binding: FragmentSettingsPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsPasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.updateButton.setOnClickListener { passwordUpdateButtonClicked() }
        binding.oldpasswordEditText.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.oldPasswordLayout.error = "Boş olamaz!"
            } else {
                binding.oldPasswordLayout.error = null
            }
        }
        binding.newPassword.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.newPasswordLayout.error = "Boş olamaz!"
            } else {
                binding.newPasswordLayout.error = null
            }
        }
        return binding.root
    }

    private fun passwordUpdateButtonClicked() {
        val previousPassword = binding.oldpasswordEditText.text.toString()
        val newPassword = binding.newPassword.text.toString()
        val repeatedPassword = binding.repeatPasswordEditText.text.toString()
        isValid(previousPassword, newPassword, repeatedPassword)
        if (!viewModel.areEmptyOrNull()) {
            viewModel.reAuthenticateUser()
        }
        viewModel.isAuthenticated.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    viewModel.updatePassword()
                }
            }
        }
        viewModel.isChanged.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    Snackbar.make(
                        requireView(),
                        R.string.şifreDeğiştirmeBaşarılı,
                        Snackbar.LENGTH_LONG
                    ).show()
                    goBackToSettings()
                } else {
                    Snackbar.make(
                        requireView(),
                        R.string.şifreDeğiştirmeBaşarısız,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun goBackToSettings() {
        val action = SettingsPasswordFragmentDirections.actionSettingsPasswordFragmentToSettingsAdminFragment()
        requireView().findNavController().navigate(action)
    }

    private fun isValid(previousPassword: String, newPassword: String, repeatedPassword: String) {
        if (newPassword.length in 1 until 8) {
            binding.newPasswordLayout.error = "Şifre en az 8 karakter olmalı!"
        } else {
            binding.newPasswordLayout.error = null
        }
        if (newPassword.isEmpty()) {
            binding.newPasswordLayout.error = "Boş olamaz!"
        } else {
            binding.newPasswordLayout.error = null
        }
        if (repeatedPassword.isEmpty()) {
            binding.repeatPasswordLayout.error = "Boş olamaz!"
        } else {
            binding.repeatPasswordLayout.error = null
        }
        if (previousPassword.isEmpty()) {
            binding.oldPasswordLayout.error = "Boş olamaz!"
        } else {
            binding.oldPasswordLayout.error = null
        }
    }
}