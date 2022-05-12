package com.graduationproject.grad_project.view.admin.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowRequestInfoDialogBinding
import com.squareup.picasso.Picasso

class ShowRequestInfoDialogFragment : DialogFragment() {

    private var _binding : FragmentShowRequestInfoDialogBinding? = null
    private val binding get() = _binding!!
    private val args: ShowRequestInfoDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowRequestInfoDialogBinding.inflate(layoutInflater)
            val view = binding.root
            binding.requestTitleText.text = args.title
            binding.requestContentText.text = args.content
            binding.requestTypeText.text = args.type
            showImage()
            val materialDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setPositiveButton(R.string.tamam) { _, _ -> }
                .create()

            materialDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun showImage() {
        if (args.downloadUri.isNotEmpty()) {
            Picasso.get().load(args.downloadUri).into(binding.imageOfRequest)
        } else {
            binding.imageOfRequest.visibility = View.GONE
        }
    }
}