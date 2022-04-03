package com.graduationproject.grad_project.view.resident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.adapter.AnnouncementRecyclerViewAdapter
import com.graduationproject.grad_project.adapter.NotificationsRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification


class NotificationResidentFragment : Fragment() {

    private var _binding: FragmentNotificationResidentBinding? = null
    private val binding get() = _binding!!
    private var notificationsRecyclerViewAdapter: NotificationsRecyclerViewAdapter? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationResidentBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    private fun adaptThisFragmentWithRecyclerView(notifications: ArrayList<Notification>) {
        binding.notificationRecyclerview.layoutManager = LinearLayoutManager(this.context)
        notificationsRecyclerViewAdapter = this.context?.let { context ->
            NotificationsRecyclerViewAdapter(notifications, context, db, auth, layoutInflater)
        }
        binding.notificationRecyclerview.adapter = notificationsRecyclerViewAdapter
    }


}