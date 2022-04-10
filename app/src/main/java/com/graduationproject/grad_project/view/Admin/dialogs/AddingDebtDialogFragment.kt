package com.graduationproject.grad_project.view.admin.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.components.SnackBars
import com.graduationproject.grad_project.databinding.FragmentAddingDebtDialogBinding
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.viewmodel.dialogs.AddingDebtDialogViewModel
import kotlinx.coroutines.*
import java.lang.IllegalStateException


class AddingDebtDialogFragment(
    private val resident: SiteResident,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : DialogFragment() {

    private var _binding: FragmentAddingDebtDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddingDebtDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentAddingDebtDialogBinding.inflate(layoutInflater)
            val view = binding.root
            val builder = MaterialAlertDialogBuilder(it)
                .setView(view)
                .setTitle(R.string.borç_ekle)
                .setPositiveButton(R.string.borç_ekle) { _, _ ->
                    positiveButtonClicked()
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
            val debtTitle = binding.debtName.text.toString()
            val debtAmount = binding.deletedAmount.text.toString().toDouble()
            viewModel.setAmount(debtAmount)
            viewModel.setTitle(debtTitle)
            /*viewModel.debtTitle.postValue(debtTitle)
            viewModel.debtAmount.postValue(debtAmount)
            println("debt${viewModel.debtAmount.value}")
            println("title${viewModel.debtTitle.value}")*/
            try {
                withContext(ioDispatcher) {
                    viewModel.addDebt(resident.email, viewModel.debtAmount)
                    if (viewModel.isDebtUpdated) {
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