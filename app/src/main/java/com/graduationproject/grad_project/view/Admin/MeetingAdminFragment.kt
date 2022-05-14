package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentMeetingAdminBinding

class MeetingAdminFragment : Fragment() {

    private var _binding: FragmentMeetingAdminBinding?  =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMeetingAdminBinding.inflate(inflater, container, false)

        return binding.root
    }

}