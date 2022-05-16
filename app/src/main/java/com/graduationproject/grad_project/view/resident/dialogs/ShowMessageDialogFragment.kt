package com.graduationproject.grad_project.view.resident.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.databinding.FragmentShowMessageDialogBinding
import com.graduationproject.grad_project.model.Message

class ShowMessageDialogFragment(
    private val message: Message
): DialogFragment() {

    private var _binding: FragmentShowMessageDialogBinding? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowMessageDialogBinding.inflate(layoutInflater)
            val builder = MaterialAlertDialogBuilder(it)
                .setView(view)
                .setTitle(message.title)
                .setMessage(message.content)
                .setPositiveButton("Tamam") { _,_ ->
                    dismiss()
                }
           builder.create()
       } ?: throw IllegalStateException("Activity cannot be null")
   }
}