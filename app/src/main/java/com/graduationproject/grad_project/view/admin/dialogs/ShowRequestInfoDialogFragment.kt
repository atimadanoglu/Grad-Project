package com.graduationproject.grad_project.view.admin.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentShowRequestInfoDialogBinding


class ShowRequestInfoDialogFragment : DialogFragment() {

    private var _binding : FragmentShowRequestInfoDialogBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowRequestInfoDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

}