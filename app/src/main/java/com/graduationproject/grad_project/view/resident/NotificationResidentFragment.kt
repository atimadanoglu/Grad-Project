package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduationproject.grad_project.adapter.NotificationsRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.viewmodel.NotificationsResidentViewModel

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
        binding.notificationRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        notificationsRecyclerViewAdapter = NotificationsRecyclerViewAdapter(parentFragmentManager, requireContext())
        binding.notificationRecyclerview.adapter = notificationsRecyclerViewAdapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.notifications.observe(viewLifecycleOwner) {
            notificationsRecyclerViewAdapter?.submitList(it)
        }
        viewModel.retrieveNotifications()
        binding.deleteNotificationButton.setOnClickListener {
           viewModel.clearNotifications()
        }
        return view
    }
}
