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
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowMessageDialogBinding.inflate(layoutInflater)
            binding.messageContentText.text = message.content
            val builder = MaterialAlertDialogBuilder(it)
               .setView(view)
               .setTitle(message.title)
               .setPositiveButton("Tamam") { _,_ ->
                   dismiss()
               }
           builder.create()
       } ?: throw IllegalStateException("Activity cannot be null")
   }
}