package com.graduationproject.grad_project.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.components.SnackBars
import com.graduationproject.grad_project.databinding.FragmentSettingsPhoneBinding
import com.graduationproject.grad_project.viewmodel.SettingsPhoneViewModel

class SettingsPhoneFragment : Fragment() {

    private var _binding: FragmentSettingsPhoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsPhoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsPhoneBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.updateButton.setOnClickListener {
            viewModel.updatePhoneNumber()
        }
        viewModel.isSuccessful.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    goToSettingsPage()
                } else {
                    Snackbar.make(
                        requireView(),
                        R.string.telefonNoDeğiştirilemedi,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
        return binding.root
    }

    private fun goToSettingsPage() {
        val action = SettingsPhoneFragmentDirections.actionSettingsPhoneFragmentToSettingsAdminFragment()
        findNavController().navigate(action)
    }
}