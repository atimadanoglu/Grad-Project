package com.graduationproject.grad_project.view.admin.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.components.SnackBars
import com.graduationproject.grad_project.databinding.FragmentDeletingDebtDialogBinding
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.viewmodel.dialogs.DeletingDebtDialogViewModel
import kotlinx.coroutines.*
import java.lang.IllegalStateException

class DeletingDebtDialogFragment(
    private val resident: SiteResident,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : DialogFragment() {
    private var _binding: FragmentDeletingDebtDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeletingDebtDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentDeletingDebtDialogBinding.inflate(layoutInflater)
            val view = binding.root
            val builder = MaterialAlertDialogBuilder(it)
                .setView(view)
                .setTitle(R.string.borç_sil)
                .setPositiveButton(R.string.borç_sil) { _, _ ->
                    positiveButtonClicked()
                    dismiss()
                }.setNegativeButton(R.string.iptal){ _, _ ->
                    negativeButtonClicked()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun negativeButtonClicked() {
        SnackBars.showDeletingDebtOperationCancelled(view)
    }

    private fun positiveButtonClicked() {
        CoroutineScope(mainDispatcher).launch {
            val deletingReason = binding.reasonOfDeleteDebtText.text.toString()
            val deletedAmount = binding.deletedAmountDeleteDebt.text.toString()
            val newAmount = if (deletedAmount.isNotEmpty()) {
                deletedAmount.toLong()
            } else {
                0
            }
            viewModel.setDeletedAmount(newAmount)
            viewModel.setCause(deletingReason)
            try {
                withContext(ioDispatcher) {
                    viewModel.deleteDebt(resident.email, newAmount)
                    if (viewModel.isDebtUpdated && deletedAmount.isNotEmpty()) {
                        viewModel.takePlayerIdAndSendPostNotification(resident)
                    }
                }
                SnackBars.showDeletingDebtOperationIsSuccessful(view)
            } catch (e: FirebaseFirestoreException) {
                SnackBars.showDeletingDebtOperationIsFailed(view)
            }
        }
    }
}