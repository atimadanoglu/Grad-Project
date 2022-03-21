package com.graduationproject.grad_project.view.admin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.core.OrderBy
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.adapter.AnnouncementRecyclerViewAdapter
import com.graduationproject.grad_project.databinding.FragmentAnnouncementsBinding
import com.graduationproject.grad_project.model.Announcement

class AnnouncementsFragment : Fragment() {

    private var _binding : FragmentAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var announcementRecyclerViewAdapter : AnnouncementRecyclerViewAdapter? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adminRef: CollectionReference

    companion object {
        private const val TAG = "AnnouncementsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        adminRef = db.collection("administrators")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.addAnnouncementButton.setOnClickListener {
            val action = AnnouncementsFragmentDirections.actionAnnouncementsFragmentToAddAnnouncementFragment()
            navController.navigate(action)
        }
        displayAnnouncements()
    }

    private fun orderAnnouncementsByDateAndFetch(email: String, announcements: ArrayList<Announcement>): Task<QuerySnapshot> {
        return adminRef.document(email)
            .collection("announcements")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null) {
                        announcements.add(
                            Announcement(
                                document.get("title") as String,
                                document.get("content") as String,
                                document.get("pictureUri") as String,
                                document.get("id") as String
                            )
                        )
                    }
                }
                adaptThisFragmentWithRecyclerView(announcements)
            }.addOnFailureListener {
                Log.w(TAG, "FAILED while getting announcements", it)
            }
    }

    private fun displayAnnouncements() {
        val currentUser = auth.currentUser
        val announcements = ArrayList<Announcement>()
        if (currentUser != null) {
            currentUser.email?.let {
                orderAnnouncementsByDateAndFetch(it, announcements)
            }
        }
    }

    private fun adaptThisFragmentWithRecyclerView(announcements: ArrayList<Announcement>) {
        binding.announcementRecyclerview.layoutManager = LinearLayoutManager(this.context)
        announcementRecyclerViewAdapter = this.context?.let { context ->
            AnnouncementRecyclerViewAdapter(announcements, context)
        }
        binding.announcementRecyclerview.adapter = announcementRecyclerViewAdapter
    }


}