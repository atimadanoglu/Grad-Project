package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.graduationproject.grad_project.adapter.NotificationsRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.viewmodel.NotificationsResidentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationResidentFragment : Fragment() {

    private var _binding: FragmentNotificationResidentBinding? = null
    private val binding get() = _binding!!
    private var notificationsRecyclerViewAdapter: NotificationsRecyclerViewAdapter? = null
    private val viewModel: NotificationsResidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationResidentBinding.inflate(inflater, container, false)
        val view = binding.root
        notificationsRecyclerViewAdapter = NotificationsRecyclerViewAdapter(parentFragmentManager, requireContext())
        binding.notificationRecyclerview.adapter = notificationsRecyclerViewAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.notifications.observe(viewLifecycleOwner) {
            notificationsRecyclerViewAdapter?.submitList(it)
        }
        binding.deleteNotificationButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                binding.notificationRecyclerview.alpha = 0F
                viewModel.clearNotifications()
                delay(4000L)
                binding.notificationRecyclerview.alpha = 1F
            }
        }
        return view
    }
}
