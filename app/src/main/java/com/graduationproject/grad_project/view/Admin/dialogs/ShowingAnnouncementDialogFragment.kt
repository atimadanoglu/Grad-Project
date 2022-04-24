package com.graduationproject.grad_project.view.admin.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowingAnnouncementDialogBinding
import com.graduationproject.grad_project.model.Announcement

class ShowingAnnouncementDialogFragment(
    private val announcement: Announcement
) : DialogFragment() {

    private var _binding: FragmentShowingAnnouncementDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentShowingAnnouncementDialogBinding.inflate(layoutInflater)
            val view = binding.root
            binding.announcementContentText.text = announcement.message
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(announcement.title)
                .setPositiveButton(R.string.tamam) { _, _ -> }
                .create()

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}