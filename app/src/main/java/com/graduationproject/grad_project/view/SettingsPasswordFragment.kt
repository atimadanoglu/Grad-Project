//package com.graduationproject.grad_project.view
//
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.widget.addTextChangedListener
//import androidx.fragment.app.viewModels
//import androidx.navigation.findNavController
//import com.google.android.material.snackbar.Snackbar
//import com.graduationproject.grad_project.databinding.FragmentSettingsPasswordBinding
//import com.graduationproject.grad_project.viewmodel.SettingsPasswordViewModel
//
//class SettingsPasswordFragment : Fragment() {
//
//    private var _binding: FragmentSettingsPasswordBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: SettingsPasswordViewModel by viewModels()
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        _binding = FragmentSettingsPasswordBinding.inflate(inflater, container, false)
//        binding.updateButton.setOnClickListener { passwordUpdateButtonClicked() }
//        binding.passwordEditText.addTextChangedListener {
//            if (it.isNullOrEmpty()) {
//                binding.passwordLayout.error = "Boş olamaz!"
//            } else {
//                binding.passwordLayout.error = null
//            }
//        }
//        binding.newPassword.addTextChangedListener {
//            if (it.isNullOrEmpty()) {
//                binding.newPasswordLayout.error = "Boş olamaz!"
//            } else {
//                binding.newPasswordLayout.error = null
//            }
//        }
//        viewModel.isAuthenticated.observe(viewLifecycleOwner) {
//            if (it) {
//                Log.d("SettingsPasswordFragment", "isAuthenticated.observe --> Authentication i")
//            } else {
//                Snackbar.make(
//                    requireView(),
//                    "Şifreniz değiştirilemedi. Şifrenizi lütfen kontrol ediniz!",
//                    Snackbar.LENGTH_LONG
//                ).show()
//            }
//        }
//        return binding.root
//    }
//
//    private fun passwordUpdateButtonClicked() {
//        val previousPassword = binding.passwordEditText.text
//        val newPassword = binding.newPassword.text
//        isValid(previousPassword.toString(), newPassword.toString())
//        if (!previousPassword.isNullOrEmpty() && !newPassword.isNullOrEmpty() && newPassword.length > 7) {
//            viewModel.setPreviousPassword(previousPassword.toString())
//            viewModel.setNewPassword(newPassword.toString())
//            viewModel.reAuthenticateAndUpdatePassword(previousPassword.toString())
//        }
//        viewModel.isChanged.observe(viewLifecycleOwner) {
//            if (it) {
//                Snackbar.make(
//                    requireView(),
//                    "Şifre değiştirme başarılı!",
//                    Snackbar.LENGTH_LONG
//                ).show()
//                goBackToSettings()
//            } else {
//                Snackbar.make(
//                    requireView(),
//                    "Şifre değiştirme başarısız!",
//                    Snackbar.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun goBackToSettings() {
//        val action = SettingsPasswordFragmentDirections.actionSettingsPasswordFragmentToSettingsAdminFragment()
//        requireView().findNavController().navigate(action)
//    }
//
//    private fun isValid(previousPassword: String, newPassword: String) {
//        if (newPassword.length in 1 until 8) {
//            binding.newPasswordLayout.error = "Şifre en az 8 karakter olmalı!"
//        } else {
//            binding.newPasswordLayout.error = null
//        }
//        if (newPassword.isEmpty()) {
//            binding.newPasswordLayout.error = "Boş olamaz!"
//        } else {
//            binding.newPasswordLayout.error = null
//        }
//        if (previousPassword.isEmpty()) {
//            binding.passwordLayout.error = "Boş olamaz!"
//        } else {
//            binding.passwordLayout.error = null
//        }
//    }
//
//}