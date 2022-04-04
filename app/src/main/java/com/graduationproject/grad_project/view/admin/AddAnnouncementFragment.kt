package com.graduationproject.grad_project.view.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentAddAnnouncementBinding
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.viewmodel.AddAnnouncementViewModel
import com.onesignal.OneSignal
import com.onesignal.OneSignal.PostNotificationResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONStringer
import java.util.*
import kotlin.collections.ArrayList


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
        binding.selectPicture.setOnClickListener(selectImageButtonClicked)
        binding.backButtonToAnnouncement.setOnClickListener { goToPreviousPage() }
        return binding.root
    }

    private fun saveAnnouncementIntoDB(adminEmail: String, announcementID: String, announcement: Announcement) {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                adminRef.document(adminEmail)
                    .collection("announcements")
                    .document(announcementID)
                    .set(announcement)
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
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
            try {
                currentUser?.email?.let { email ->
                    saveAnnouncementIntoDB(email, announcement.id, announcement)
                    println("shareannouncementresident üstyü")

                    shareAnnouncementWithResidents()
                    println("shareAnnouncemnets altı")

                    uploadImage()

                    lifecycleScope.launch {
                        val playerIDs = takePlayerIDs(email)
                        postNotification(playerIDs, announcement)
                    }
                    goToPreviousPage()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    /**
     * It will be used to send push notification to residents by using
     * their player_ids
     * @param adminEmail It's for taking the admin's info from db
     * * */
    private suspend fun takePlayerIDs(adminEmail: String): ArrayList<String> {
        val admin = getAdmin(adminEmail)
        val residents = admin?.let { getResidents(it) }
        val residentDocuments = residents?.documents
        val playerIDs = arrayListOf<String>()
        if (residentDocuments != null) {
            for (document in residentDocuments) {
                playerIDs.add(document["player_id"].toString())
            }
        }
        return playerIDs
    }

    private fun createJsonObjectForNotification(title: String, message: String, playerIDs: ArrayList<String>): JSONObject {
        val jsonObject = JSONObject(
            "{'headings': {'en': '$title'}," +
                    "'contents': {'en': '$message'}," +
                    "'include_player_ids': '[]'}"
        )
        val jsonArray = JSONArray()
        for(playerID in playerIDs) {
            jsonArray.put(playerID)
        }
        jsonObject.put("include_player_ids", jsonArray)
        return jsonObject
    }

    private fun postNotification(
        playerIDs: ArrayList<String>,
        announcement: Announcement
    ) {
        try {
            val pushNotificationJsonOneSignal =
                createJsonObjectForNotification(announcement.title, announcement.message, playerIDs)
            OneSignal.postNotification(
                pushNotificationJsonOneSignal,
                object : PostNotificationResponseHandler {
                    override fun onSuccess(response: JSONObject) {
                        Log.i("OneSignalExample", "postNotification Success: $response")
                    }

                    override fun onFailure(response: JSONObject) {
                        Log.e("OneSignalExample", "postNotification Failure: $response")
                    }
                })
        } catch (e: JSONException) {
            e.printStackTrace()
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
            try {
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                    Log.d(tag, "Images successfully uploaded!")
                }.addOnFailureListener {
                    Log.w(tag, it.localizedMessage, it)
                    Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
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

    private suspend fun getResidents(adminInfo: DocumentSnapshot): QuerySnapshot? {
        return residentRef.whereEqualTo("city", adminInfo["city"])
            .whereEqualTo("district", adminInfo["district"])
            .whereEqualTo("siteName", adminInfo["siteName"])
            .get().await()
    }

    private suspend fun getAdmin(email: String): DocumentSnapshot? {
        return try {
            val admin = adminRef.document(email)
                .get()
                .await()
            admin
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    private suspend fun saveNotificationIntoResidentDB(emailsOfResidents: ArrayList<String>, notification: Announcement) {
        for (emailOfResident in emailsOfResidents) {
            try {
                residentRef.document(emailOfResident)
                    .collection("notifications")
                    .document(notification.id)
                    .set(notification)
                    .addOnSuccessListener {
                        Log.d(TAG, "Announcement write is SUCCESSFUL!")
                    }.addOnFailureListener { exception ->
                        Log.w(TAG, "Announcement write is UNSUCCESSFUL!", exception)
                    }.await()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private suspend fun shareAnnouncementWithResidents() {
        try {
            val email = auth.currentUser?.email
            email?.let {
                getAdmin(email).also {
                    val adminInfo = it
                    adminInfo?.let {
                        getResidents(adminInfo).also { residents ->
                            Log.d(TAG, "get() successfully worked")
                            val emails = arrayListOf<String>()

                            if (residents != null) {
                                for (resident in residents) {
                                    emails.add(resident["email"] as String)
                                }
                            }
                            val notification = getAnnouncementInfo()
                            saveNotificationIntoResidentDB(emails, notification)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
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