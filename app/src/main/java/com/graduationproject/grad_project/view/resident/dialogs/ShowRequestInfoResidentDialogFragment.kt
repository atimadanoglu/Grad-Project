package com.graduationproject.grad_project.view.resident.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowRequestInfoResidentDialogBinding
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.viewmodel.dialogs.ShowRequestInfoResidentDialogViewModel
import java.lang.IllegalStateException


class ShowRequestInfoResidentDialogFragment(
    private val request: Request
) : DialogFragment() {

    private var _binding: FragmentShowRequestInfoResidentDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowRequestInfoResidentDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowRequestInfoResidentDialogBinding.inflate(layoutInflater)
            val view = binding.root
            viewModel.setTitle(request.title)
            viewModel.setContent(request.content)
            viewModel.setType(request.type)
            binding.requestTitleText.text = viewModel.title
            binding.requestContentText.text = viewModel.content
            binding.requestTypeText.text = viewModel.type
            val dialog = MaterialAlertDialogBuilder(it)
                .setView(view)
                .setTitle(R.string.talep_bilgisi)
                .setPositiveButton(R.string.tamam) { _, _ ->
                    dismiss()
                }

            dialog.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}