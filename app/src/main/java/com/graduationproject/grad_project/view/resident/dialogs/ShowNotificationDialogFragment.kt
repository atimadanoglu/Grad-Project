package com.graduationproject.grad_project.view.resident.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.graduationproject.grad_project.databinding.FragmentShowNotificationDialogBinding

class ShowNotificationDialogFragment : DialogFragment() {

    private var _binding: FragmentShowNotificationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowNotificationDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

}