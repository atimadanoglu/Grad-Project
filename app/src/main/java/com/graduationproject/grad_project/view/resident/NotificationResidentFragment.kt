package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.adapter.NotificationsRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.viewmodel.NotificationsResidentViewModel
import kotlinx.coroutines.launch


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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            auth.currentUser?.email?.let { viewModel.retrieveNotifications(it) }
        }
        viewModel.notifications.value?.let { adaptThisFragmentWithRecyclerView(it) }
        observeLiveData()
    }

    private fun adaptThisFragmentWithRecyclerView(notifications: ArrayList<Notification>) {
        binding.notificationRecyclerview.layoutManager = LinearLayoutManager(this.context)
        notificationsRecyclerViewAdapter = this.context?.let { context ->
            NotificationsRecyclerViewAdapter(notifications, context, db, auth, layoutInflater)
        }
        binding.notificationRecyclerview.adapter = notificationsRecyclerViewAdapter
    }

    private fun observeLiveData() {
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            notifications?.let {
                notificationsRecyclerViewAdapter?.updateNotificationList(notifications)
            }
        }
    }

}