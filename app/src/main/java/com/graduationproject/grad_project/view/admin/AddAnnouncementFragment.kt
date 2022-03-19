package com.graduationproject.grad_project.view.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import java.util.*
import kotlin.collections.HashMap


class AddAnnouncementFragment : Fragment() {

    private var _binding : FragmentAddAnnouncementBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String> // Manifest.permission.READ_EXTERNAL_STORAGE is a String
    var selectedPicture : Uri? = null
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareAnnouncementButton.setOnClickListener(shareAnnouncementButtonClicked)
        binding.selectPicture.setOnClickListener(selectImageButtonClicked)
        binding.backButtonToAnnouncement.setOnClickListener {
            goToPreviousPage()
        }

    }

    private fun shareAnnouncementButtonClicked(view: View) {
        val currentUser = auth.currentUser
        val announcement = getAnnouncementInfo()

        // Write announcement to DB
        currentUser?.email?.let { it1 ->
            db.collection("administrators")
                .document(it1)
                .collection("announcements")
                .document(announcement["id"] as String)
                .set(announcement)
                .addOnSuccessListener {
                    Log.d("AddAnnouncementFragment", "Announcement document successfully written!")
                }.addOnFailureListener {
                    Log.w("AddAnnouncementFragment", "Announcement document failed while being written!", it)
                }
            shareAnnouncementWithResidents()
            uploadImage()
            goToPreviousPage()
        }
    }

    private val selectImageButtonClicked = View.OnClickListener { p0 ->
        if (p0 != null) {
            selectImageButtonClicked(p0)
        }
    }

    private val shareAnnouncementButtonClicked = View.OnClickListener {
        shareAnnouncementButtonClicked(it)
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(this.context, "Permission needed", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun selectImageButtonClicked(view : View) {

        if (ContextCompat
                .checkSelfPermission(
                    view.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give permission", View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                // request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun uploadImage() {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val reference = storage.reference
        val imageReference = reference.child("announcementDocuments").child(imageName)

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                Log.d(tag, "Images successfully uploaded!")
            }.addOnFailureListener {
                Log.w(tag, it.localizedMessage, it)
                Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getAnnouncementInfo(): HashMap<String, Any> {
        val uuid = UUID.randomUUID()

        return hashMapOf(
            "title" to binding.titleInput.text.toString(),
            "content" to binding.contentInput.text.toString(),
            "pictureUri" to selectedPicture.toString(),
            "id" to uuid.toString(),
            "date" to Timestamp(Date())
        )
    }

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


                            // TODO: You should send push notifications to users instead of saving
                            // the notifications into their DB collection. All are ready here.
                             // Only you will send push notification to users by using their emails.
                              // And try to save the announcements into sites collection and retrieve
                             //  them into announcements for site residents
                            for (emailOfResident in emails) {
                                db.collection("residents")
                                    .document(emailOfResident)
                                    .collection("announcements")
                                    .document()
                                    .set(announcement)
                                    .addOnSuccessListener {
                                        Log.d("AddAnnouncementFragment", "Announcement write is SUCCESSFUL!")
                                    }.addOnFailureListener { exception ->
                                        Log.w("AddAnnouncementFragment", "Announcement write is UNSUCCESSFUL!", exception)
                                    }
                            }
                        }
                }
        }
    }

    private fun goToPreviousPage() {
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val action = AddAnnouncementFragmentDirections.actionAddAnnouncementFragmentToAnnouncementsFragment()
        navController.navigate(action)
    }
}