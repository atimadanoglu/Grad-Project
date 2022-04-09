package com.graduationproject.grad_project.view.resident.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R

class ShowMessageDialogFragment(
    val title: String,
    val content: String
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_show_message_dialog, null)
            val a = view.findViewById<TextView>(R.id.message_content_text)
            a?.text = content
            val builder = MaterialAlertDialogBuilder(it)
               .setView(view)
               .setTitle(title)
               .setPositiveButton("Tamam") { _,_ ->
                   dismiss()
               }
           builder.create()
       } ?: throw IllegalStateException("Activity cannot be null")
   }
}