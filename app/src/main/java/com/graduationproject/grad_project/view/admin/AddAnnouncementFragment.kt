package com.graduationproject.grad_project.view.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap


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

        // When share announcement button clicked
        binding.shareAnnouncementButton.setOnClickListener {
            val currentUser = auth.currentUser

            val announcement = getAnnouncementInfo()

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
            shareAnnouncementWithResidents()
            goToPreviousPage()
        }

    }

    private fun getAnnouncementInfo(): HashMap<String, Any> {
        return hashMapOf(
            "title" to binding.titleInput.text.toString(),
            "content" to binding.contentInput.text.toString(),
            "date" to Timestamp(Date())
        )
    }

    //
    private fun shareAnnouncementWithResidents() {
        auth.currentUser?.email?.let { email ->
            db.collection("administrators")
                .document(email)
                .get()

                .addOnSuccessListener {
                    val adminInfo = it
                    Log.d("admin","Admin info retrieved")
                    db.collection("residents")
                        .whereEqualTo("city", adminInfo["city"])
                        .whereEqualTo("district", adminInfo["district"])
                        .whereEqualTo("siteName", adminInfo["siteName"])
                        .get()

                        .addOnSuccessListener { residents ->
                            Log.d("AddAnnouncementFragment", "get() successfully worked")
                            val emails = mutableListOf<String>()
                            for (resident in residents) {
                                emails.add(resident["email"] as String)
                            }

                            val announcement = getAnnouncementInfo()

                            /*
                            todo : You should send push notifications to users instead of saving
                             the notifications into their DB collection. All are ready here.
                              Only you will send push notification to users by using their emails.
                               And try to save the announcements into sites collection and retrieve
                                them into announcements for site residents
                            */
                            for (emailOfResident in emails) {
                                db.collection("residents")
                                    .document(emailOfResident)
                                    .collection("announcements")
                                    .document()
                                    .set(announcement)
                                    .addOnSuccessListener {
                                        Log.d("AddAnnouncementFragment", "Announcement write is SUCCESSFUL!")
                                    }.addOnFailureListener {
                                        Log.w("AddAnnouncementFragment", "Announcement write is UNSUCCESSFUL!", it)
                                    }
                            }
                        }
                }
        }
    }



    private fun goToPreviousPage() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFragmentContainerView, AnnouncementsFragment()).commit()
    }
}