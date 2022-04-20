package com.graduationproject.grad_project.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentSuccessfulDialogBinding

class SuccessfulDialogFragment : DialogFragment() {

    private var _binding: FragmentSuccessfulDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentSuccessfulDialogBinding.inflate(layoutInflater)
            val view = binding.root
            val args: SuccessfulDialogFragmentArgs by navArgs()
            val text = "Borcunuz için ${args.paidAmount} TL ödeme yaptınız."
            binding.payDebtInfoText.text = text
            val dialog = MaterialAlertDialogBuilder(it)
                .setView(view)
                .setPositiveButton(R.string.tamam) { _, _ ->
                    findNavController()
                        .navigate(SuccessfulDialogFragmentDirections.actionSuccessfulDialogFragmentToPayDebtFragment())
                }
                .create()
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}