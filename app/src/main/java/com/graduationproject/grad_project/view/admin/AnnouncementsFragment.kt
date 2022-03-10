package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.AnnouncementRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentAnnouncementsBinding
import com.graduationproject.grad_project.model.Announcement

class AnnouncementsFragment : Fragment() {

    private var _binding : FragmentAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var announcements : ArrayList<Announcement>? = null
    private var announcementRecyclerViewAdapter : AnnouncementRecyclerViewAdapter? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        println("oncreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")
        retrieveAndShowAnnouncements()
    }

    private fun retrieveAndShowAnnouncements() {
        val currentUser = auth.currentUser
        val announcements = ArrayList<Announcement>()
        if (currentUser != null) {
            currentUser.email?.let {
                db.collection("administrators")
                    .document(it)
                    .collection("announcements")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document != null) {
                                announcements.add(Announcement(
                                    document.get("title") as String,
                                    document.get("content") as String
                                ))
                                println(document.get("title"))
                                println(document.get("content"))
                            }
                            announcementRecyclerViewAdapter = AnnouncementRecyclerViewAdapter(announcements)
                            binding.announcementRecyclerview.adapter = announcementRecyclerViewAdapter
                        }
                    }
            }
        }
    }


}