package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graduationproject.grad_project.adapter.NotificationsAdminAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationsAdminBinding
import com.graduationproject.grad_project.viewmodel.NotificationsAdminViewModel

class NotificationsAdminFragment : Fragment() {

    private var _binding: FragmentNotificationsAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsAdminViewModel by viewModels()
    private lateinit var adapter: NotificationsAdminAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationsAdminBinding.inflate(inflater, container, false)
        adapter = NotificationsAdminAdapter(parentFragmentManager, requireContext())
        viewModel.notifications.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.retrieveNotifications()
        binding.notificationRecyclerview.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}