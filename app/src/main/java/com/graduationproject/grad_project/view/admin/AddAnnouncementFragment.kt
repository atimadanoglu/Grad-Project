package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import java.util.*


class AddAnnouncementFragment : Fragment() {

    private var _binding : FragmentAddAnnouncementBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareAnnouncementButton.setOnClickListener {
            val currentUser = auth.currentUser
            val announcement = hashMapOf<String, Any>(
                "title" to binding.titleInput.text.toString(),
                "content" to binding.contentInput.text.toString(),
                "date" to Timestamp(Date())
            )

            // Write announcement to DB
            currentUser?.email?.let { it1 ->
                db.collection("administrators")
                    .document(it1)
                    .collection("announcements")
                    .document()
                    .set(announcement)
                    .addOnSuccessListener {
                        Log.d("AddAnnouncementFragment", "Announcement document successfully written!")
                    }.addOnFailureListener {
                        Log.w("AddAnnouncementFragment", "Announcement document failed while being written!", it)
                    }
            }
            goToPreviousPage()
        }

    }

    private fun goToPreviousPage() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.hostFragment, AnnouncementsFragment()).commit()
    }
}