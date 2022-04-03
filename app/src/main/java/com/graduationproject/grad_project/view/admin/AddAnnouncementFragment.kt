package com.graduationproject.grad_project.view.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.grad_project.FirebaseService
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import com.graduationproject.grad_project.model.Administrator
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.PushNotification
import com.graduationproject.grad_project.viewmodel.AddAnnouncementViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*


class AddAnnouncementFragment : Fragment() {

    private var _binding : FragmentAddAnnouncementBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String> // Manifest.permission.READ_EXTERNAL_STORAGE is a String
    private var selectedPicture : Uri? = null
    private lateinit var storage : FirebaseStorage
    private lateinit var adminRef: CollectionReference
    private lateinit var residentRef: CollectionReference
    private lateinit var viewModel: AddAnnouncementViewModel

    companion object {
        private const val TAG = "AddAnnouncementFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        adminRef = db.collection("administrators")
        residentRef = db.collection("residents")
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddAnnouncementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AddAnnouncementViewModel::class.java]

        binding.shareAnnouncementButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                shareAnnouncementButtonClicked()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.selectPicture.setOnClickListener(selectImageButtonClicked)
        binding.backButtonToAnnouncement.setOnClickListener {
            goToPreviousPage()
        }

    }

    private suspend fun shareAnnouncementButtonClicked() {
        val currentUser = auth.currentUser
        val announcement = getAnnouncementInfo()
        val title = binding.titleInput.text
        val content = binding.contentInput.text
        if (title.isBlank() || content.isBlank()) {
            Toast.makeText(this.context, "Lütfen boş alanları doldurunuz!", Toast.LENGTH_LONG).show()
        } else {
            // Write announcement to DB
            currentUser?.email?.let { email ->
                adminRef.document(email)
                    .collection("announcements")
                    .document(announcement.id)
                    .set(announcement)
                    .addOnSuccessListener {
                        Log.d(TAG, "Announcement document successfully written!")
                    }.addOnFailureListener {
                        Log.w(TAG, "Announcement document failed while being written!", it)
                    }
                shareAnnouncementWithResidents()
                uploadImage()
                this.context?.let {
                    viewModel.registerTheDeviceAndTopicAndSendItToUsers(
                        it,
                        TAG,
                        getTopicName(email),
                        getNotificationInfo()
                    )
                }
              /*  FirebaseService.sharedPreferences = context?.getSharedPreferences("getSharedPreferences", MODE_PRIVATE)
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result

                    // Log and toast
                    FirebaseService.token = token
                    Toast.makeText(this.context, token, Toast.LENGTH_SHORT).show()
                })
                val topic = getTopicName(email)
                topic?.let { FirebaseMessaging.getInstance().subscribeToTopic(it) }
                topic?.let {
                    FirebaseService.token?.let { it1 ->
                        PushNotification(
                            getNotificationInfo(),
                            it1
                        ).also { pushNotification ->
                            viewModel.sendNotification(pushNotification)
                        }
                    }
                }*/
                goToPreviousPage()
            }

        }
    }

    private val selectImageButtonClicked = View.OnClickListener { p0 ->
        if (p0 != null) {
            selectImageButtonClicked(p0)
        }
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
                    .setAction("Give permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
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

    private fun getAnnouncementInfo(): Announcement {
        val uuid = UUID.randomUUID()
        return Announcement(
            binding.titleInput.text.toString(),
            binding.contentInput.text.toString(),
            selectedPicture.toString(),
            uuid.toString(),
            Timestamp(Date())
        )
    }

    private suspend fun getTopicName(email: String): String? {
        return try {
            val snapshot = getAdmin(email)
            var topic = ""
            if (snapshot != null) {
                val admin = snapshot.toObject<Administrator>()
                println("${admin?.city}")
                if (admin != null) {
                    println("${admin.uid}")
                }
                //siteName:${site["siteName"]}-city:${site["city"]}-district:${site["district"]}
                topic = "siteName:${admin?.siteName}-city:${admin?.city}-district:${admin?.district}"
            }
            topic
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            null
        }
    }

    private fun getNotificationInfo(): Notification {
        val uuid = UUID.randomUUID()
        return Notification(
            binding.titleInput.text.toString(),
            binding.contentInput.text.toString(),
            selectedPicture.toString(),
            uuid.toString(),
            Timestamp(Date())
        )
    }

    private fun getResidents(adminInfo: DocumentSnapshot): Task<QuerySnapshot> {
        return residentRef.whereEqualTo("city", adminInfo["city"])
            .whereEqualTo("district", adminInfo["district"])
            .whereEqualTo("siteName", adminInfo["siteName"])
            .get()
    }

    private suspend fun getAdmin(email: String): DocumentSnapshot? {
        return try {
            val admin = adminRef.document(email)
                .get()
                .await()
            admin
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun shareAnnouncementWithResidents(): Boolean {
        return try {
            auth.currentUser?.email?.let { email ->
                getAdmin(email).also {
                    val adminInfo = it
                    Log.d("admin", "Admin info retrieved")
                    if (adminInfo != null) {
                        getResidents(adminInfo)
                            .addOnSuccessListener { residents ->
                                Log.d(TAG, "get() successfully worked")
                                val emails = arrayListOf<String>()
                                for (resident in residents) {
                                    emails.add(resident["email"] as String)
                                }
                                val notification = getAnnouncementInfo()
                                for (emailOfResident in emails) {
                                    residentRef.document(emailOfResident)
                                        .collection("notifications")
                                        .document(notification.id)
                                        .set(notification)
                                        .addOnSuccessListener {
                                            Log.d(
                                                TAG,
                                                "Announcement write is SUCCESSFUL!"
                                            )
                                        }.addOnFailureListener { exception ->
                                            Log.w(
                                                TAG,
                                                "Announcement write is UNSUCCESSFUL!",
                                                exception
                                            )
                                        }
                                }
                            }
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            false
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