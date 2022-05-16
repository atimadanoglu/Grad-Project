package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.graduationproject.grad_project.databinding.FragmentAddVotingAdminBinding
import com.graduationproject.grad_project.view.admin.dialogs.DatePickerDialogFragment
import com.graduationproject.grad_project.viewmodel.AddVotingViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AddVotingAdminFragment : Fragment() {

    private var _binding: FragmentAddVotingAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddVotingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddVotingAdminBinding.inflate(inflater, container, false)
        datePickerSetOnClickListener()
        shareVotingButtonSetOnClickListener()
        binding.backButtonVoting.setOnClickListener {
            goBackToVotingPage()
        }
        viewModel.chosenDate.observe(viewLifecycleOwner) {
            binding.datePicker.text = it
        }
        return binding.root
    }

    private fun goBackToVotingPage() {
        val action = AddVotingAdminFragmentDirections.actionAddVotingAdminFragmentToVotingAdminFragment()
        requireView().findNavController().navigate(action)
    }

    private fun datePickerSetOnClickListener() {
        binding.datePicker.setOnClickListener {
            val dialog = DatePickerDialogFragment(viewModel)
            dialog.show(parentFragmentManager, "datePicker")
        }
    }

    private fun shareVotingButtonSetOnClickListener() {
        binding.shareVotingButton.setOnClickListener {
            val title = binding.titleText.text.toString()
            val content = binding.contentText.text.toString()
            val date = binding.datePicker.text.toString()
            viewModel.setValues(title, content)
            checkTheInputsAreNotEmpty(title, content, date)
            if (isAllValid()) {
                viewModel.saveVotingIntoDB(title, content)
                goBackToVotingPage()
            }
        }
    }

    private fun isAllValid(): Boolean {
        val title = binding.titleText.text
        val content = binding.contentText.text
        val date = binding.datePicker.text
        return !title.isNullOrEmpty() && !content.isNullOrEmpty()
                && date.substring((date.length - 4) until date.length).isDigitsOnly()
    }

    private fun checkTheInputsAreNotEmpty(title: String, content: String, date: String) {
        if (title.isEmpty()) {
            binding.textInputLayoutTitle.error = "Geçersiz"
        } else {
            binding.textInputLayoutTitle.error = null
        }

        if (content.isEmpty()) {
            binding.textInputLayoutContent.error = "Geçersiz"
        } else {
            binding.textInputLayoutContent.error = null
        }

        if (!date.substring((date.length - 4) until date.length).isDigitsOnly()) {
            binding.datePicker.error = "Hata"
        } else {
            binding.datePicker.error = null
        }
    }

}