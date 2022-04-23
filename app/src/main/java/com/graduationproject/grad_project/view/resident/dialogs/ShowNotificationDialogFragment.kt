package com.graduationproject.grad_project.view.resident.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowNotificationDialogBinding
import com.graduationproject.grad_project.model.Notification
import com.squareup.picasso.Picasso

class ShowNotificationDialogFragment(
    private val notification: Notification
) : DialogFragment() {

    private var _binding: FragmentShowNotificationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowNotificationDialogBinding.inflate(layoutInflater)
            val view = binding.root
            binding.notificationDialogContent.text = notification.message
            showImage()
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(notification.title)
                .setPositiveButton(R.string.tamam){ _, _ ->}
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun showImage() {
        if (notification.pictureUri == null) {
            binding.notificationImage.visibility = View.GONE
        } else {
            binding.notificationImage.visibility = View.VISIBLE
            Picasso.get().load(notification.pictureUri).into(binding.notificationImage)
            println("uri : ${notification.pictureUri}")
        }
    }

}