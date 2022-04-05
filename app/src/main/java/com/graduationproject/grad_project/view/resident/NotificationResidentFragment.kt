package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.adapter.NotificationsRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.viewmodel.NotificationsResidentViewModel

class NotificationResidentFragment : Fragment() {

    private var _binding: FragmentNotificationResidentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var notificationsRecyclerViewAdapter: NotificationsRecyclerViewAdapter? = null
    private val viewModel: NotificationsResidentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationResidentBinding.inflate(inflater, container, false)
        val view = binding.root
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        binding.deleteNotificationButton.setOnClickListener {
            email?.let { email -> clearNotifications(email) }
        }
        observeLiveData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser?.email?.let { viewModel.retrieveNotifications(it) }
        viewModel.notifications.let { liveDataOfNotifications ->
            liveDataOfNotifications.value?.let { notifications -> adaptThisFragmentWithRecyclerView(notifications) }
            liveDataOfNotifications.value?.let { notifications -> notificationsRecyclerViewAdapter?.updateNotificationList(notifications) }
        }
    }
    private fun clearNotifications(email: String) {
        viewModel.clearNotifications(email)
        notificationsRecyclerViewAdapter?.updateNotificationList(arrayListOf())
    }

    private fun adaptThisFragmentWithRecyclerView(notifications: ArrayList<Notification>) {
        binding.notificationRecyclerview.layoutManager = LinearLayoutManager(this.context)
        notificationsRecyclerViewAdapter = this.context?.let { context ->
            NotificationsRecyclerViewAdapter(notifications, context)
        }
        binding.notificationRecyclerview.adapter = notificationsRecyclerViewAdapter
    }

    private fun observeLiveData() {
        viewModel.notifications.observe(viewLifecycleOwner) { arrayListOfNotifications ->
            arrayListOfNotifications?.let {
                it.let { notifications -> notificationsRecyclerViewAdapter?.updateNotificationList(notifications) }
            }
        }
    }

}