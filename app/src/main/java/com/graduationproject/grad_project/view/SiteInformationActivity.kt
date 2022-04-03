package com.graduationproject.grad_project.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.graduationproject.grad_project.databinding.ActivitySiteInformationBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import kotlinx.coroutines.*
import java.util.*


class SiteInformationActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySiteInformationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var user: HashMap<String, String> = hashMapOf()
    private var site: HashMap<String, Any> = hashMapOf()
    private var admin: HashMap<String, Any?> = hashMapOf()

    companion object {
        private const val TAG = "SiteInformationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiteInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener { signUpButtonClicked() }
        binding.backToResidentNewAccountActivityButton.setOnClickListener { backToAdminNewAccountActivityButtonClicked() }
    }

    private fun getUserData(): HashMap<String, String> {
        val i = intent.extras
        var fullName = ""
        var phoneNumber = ""
        var email = ""
        var password = ""

        if (i != null) {
            println("intent içindeyim")
            fullName = i.getString("fullName").toString()
            phoneNumber = i.getString("phoneNumber").toString()
            email = i.getString("email").toString()
            password = i.getString("password").toString()
        }
        return hashMapOf(
            "fullName" to fullName,
            "phoneNumber" to phoneNumber,
            "email" to email,
            "password" to password
        )
    }

    private fun getSiteData(): HashMap<String, Any> {
        val city = binding.cityText.text.toString()
        val district = binding.countyText.text.toString()
        val siteName = binding.siteNameText.text.toString()
        val blockCount = binding.blockCountText.text.toString()
        val flatCount = binding.flatCountText.text.toString().toInt()

        return hashMapOf(
            "siteName" to siteName,
            "city" to city,
            "district" to district,
            "blockCount" to blockCount,
            "flatCount" to flatCount,
        )
    }

    private fun isBlank(): Boolean {
        return binding.cityText.text.isBlank() || binding.countyText.text.isBlank() || binding.siteNameText.text.isBlank() ||
                binding.flatCountText.text.isBlank() || binding.blockCountText.text.isBlank()
    }

    private fun signUpButtonClicked() {
        if (isBlank()) {
            Toast.makeText(this, "Lütfen gerekli tüm kısımları doldurunuz!!!", Toast.LENGTH_LONG).show()
        } else {
            user = getUserData()
            site = getSiteData()

            admin = hashMapOf(
                "fullName" to user["fullName"],
                "phoneNumber" to user["phoneNumber"],
                "email" to user["email"],
                "password" to user["password"],
                "typeOfUser" to "Yönetici",
                "siteName" to site["siteName"],
                "city" to site["city"],
                "district" to site["district"],
                "blockCount" to site["blockCount"],
                "flatCount" to site["flatCount"]
            )
            createUser()
        }

    }

    private fun createUser() = CoroutineScope(Dispatchers.IO).launch {
        auth.createUserWithEmailAndPassword(user["email"].toString(), user["password"].toString())
            .addOnSuccessListener {
                val uid = it.user?.uid
                admin["uid"] = uid.toString()
                db.collection("sites")
                    .document("siteName:${site["siteName"]}-city:${site["city"]}-district:${site["district"]}").set(site)
                    .addOnSuccessListener {
                        Log.d(TAG, "Site document successfully written!")
                        // Write admin info into DB
                        saveAdminIntoDB(admin["email"].toString(), this@SiteInformationActivity)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Site document couldn't be written", exception)
                        Toast.makeText(this@SiteInformationActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Log.w(TAG, "User couldn't be created", it)
                Toast.makeText(this@SiteInformationActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    /*private fun savePlayerId() {
        val uuid = UUID.randomUUID().toString()
        admin["player_id"] = uuid
        OneSignal.setExternalUserId(
            uuid,
            object : OSExternalUserIdUpdateCompletionHandler {
                override fun onSuccess(results: JSONObject) {
                    try {
                        if (results.has("push") && results.getJSONObject("push").has("success")) {
                            val isPushSuccess = results.getJSONObject("push").getBoolean("success")
                            OneSignal.onesignalLog(
                                OneSignal.LOG_LEVEL.VERBOSE,
                                "Set external user id for push status: $isPushSuccess"
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onFailure(error: ExternalIdError) {
                    // The results will contain channel failure statuses
                    // Use this to detect if external_user_id was not set and retry when a better network connection is made
                    OneSignal.onesignalLog(
                        OneSignal.LOG_LEVEL.VERBOSE,
                        "Set external user id done with error: $error"
                    )
                }
            })
    }*/

    private fun updateUserInfo() {
        val currentUser = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = admin["fullName"].toString()
        }
        currentUser?.updateProfile(profileUpdates)
    }

    private fun saveAdminIntoDB(email: String, activity: Activity) = CoroutineScope(Dispatchers.IO).launch {
        db.collection("administrators")
            .document(email)
            .set(admin)
            .addOnSuccessListener {
                Log.d(TAG, "Administrator document successfully written!")
                updateUserInfo()
                val intent = Intent(activity, HomePageAdminActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(activity, "Yönetici oluşturulamadı!", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Error as writing admin info document", e)
            }
    }

    private fun backToAdminNewAccountActivityButtonClicked() {
        val intent = Intent(this, AdministratorNewAccountActivity::class.java)
        startActivity(intent)
    }
/*
    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges?) {
        if (!stateChanges?.from?.isSubscribed!! &&
            stateChanges.to.isSubscribed
        ) {
            AlertDialog.Builder(this)
                .setMessage("You've successfully subscribed to push notifications!")
                .show()
            // get player ID
            stateChanges.to.userId
        }
        Log.i("Debug", "onOSSubscriptionChanged: $stateChanges")
    }*/
}