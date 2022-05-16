package com.graduationproject.grad_project.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentSettingsResidentPhoneBinding
import com.graduationproject.grad_project.viewmodel.SettingsResidentPhoneViewModel

class SettingsResidentPhoneFragment : Fragment() {

    private var _binding: FragmentSettingsResidentPhoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsResidentPhoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsResidentPhoneBinding.inflate(inflater, container, false)
        val args: SettingsResidentPhoneFragmentArgs by navArgs()
        viewModel.phoneNumber.postValue(args.phoneNumber)
        binding.phoneEditText.setText(args.phoneNumber)
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
        val action = SettingsResidentPhoneFragmentDirections.actionSettingsResidentPhoneFragmentToSettingsResidentFragment()
        findNavController().navigate(action)
    }
}