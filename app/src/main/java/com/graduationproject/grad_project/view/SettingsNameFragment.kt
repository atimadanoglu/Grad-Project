package com.graduationproject.grad_project.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.DrawerHeaderAdminBinding
import com.graduationproject.grad_project.databinding.FragmentSettingsNameBinding
import com.graduationproject.grad_project.viewmodel.SettingsNameViewModel


class SettingsNameFragment : Fragment() {

    private var _binding: FragmentSettingsNameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsNameViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsNameBinding.inflate(inflater, container, false)
        val args: SettingsNameFragmentArgs by navArgs()
        binding.nameEditText.setText(args.fullName)
        binding.nameEditText.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.nameLayout.error = "Boş girilemez!"
            } else {
                binding.nameLayout.error = null
            }
        }
        binding.updateButton.setOnClickListener { updateButtonClicked() }
        return binding.root
    }

    private fun updateButtonClicked() {
        val fullName = binding.nameEditText.text.toString()
        viewModel.setName(fullName)
        viewModel.checkPreviousAndNewName()
        isValid(fullName)
        if (fullName.isNotEmpty() && viewModel.isSame.value == false) {
            updateDrawerHeader()
            viewModel.updateName(fullName)
            goBackToSettingsPage()
        }
    }
    private fun isValid(fullName: String) {
        if (fullName.isEmpty()) {
            binding.nameLayout.error = "Boş girilemez!"
        } else {
            binding.nameLayout.error = null
        }
        if (viewModel.isSame.value == true) {
            binding.nameLayout.error = "Aynı isim girilemez!"
        } else {
            binding.nameLayout.error = null
        }
    }

    private fun goBackToSettingsPage() {
        val action = SettingsNameFragmentDirections
            .actionSettingsNameFragmentToSettingsAdminFragment()
        findNavController().navigate(action)
    }

    private fun isValid() {
        if (viewModel.isSame.value == true) {
            binding.nameLayout.error = "Aynı isim girilemez!"
        } else {
            binding.nameLayout.error = null
        }
    }

    /**
     * It can be used to update drawer header's fullName attribute
     * after updating that data
     * */
    private fun updateDrawerHeader() {
        val header = requireActivity().findViewById<View>(R.id.drawerLayoutAdmin)
        val binding = DataBindingUtil.bind<DrawerHeaderAdminBinding>(header)
        binding?.headerAccountName?.text = viewModel.name.value
    }
}