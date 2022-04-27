package com.graduationproject.grad_project.view.resident.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowVoteResidentDialogBinding
import com.graduationproject.grad_project.model.Voting
import com.graduationproject.grad_project.viewmodel.dialogs.ShowVoteResidentDialogViewModel
import java.util.*

class ShowVoteResidentDialogFragment(
    private val voting: Voting
): DialogFragment() {

    private var _binding: FragmentShowVoteResidentDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowVoteResidentDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowVoteResidentDialogBinding.inflate(layoutInflater)
            val view = binding.root
            binding.voting = voting
            binding.dialog = this
            val materialDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setPositiveButton(R.string.gönder) {_, _ ->
                    positiveButtonClicked()
                }.create()

            materialDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun calculateDuration(voting: Voting): String {
        val date = Date()
        val diff: Long = voting.date - date.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return "$hours saat"
    }

    private fun positiveButtonClicked() {
        when (binding.chipGroup.checkedChipId) {
            R.id.acceptChip -> {
                viewModel.acceptButtonClicked(voting)
            }
            R.id.rejectChip -> {
                viewModel.rejectButtonClicked(voting)
            }
        }
    }

}