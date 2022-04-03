package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.graduationproject.grad_project.databinding.ActivitySiteInformationResidentBinding
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import com.onesignal.OneSignal
import java.util.*
import kotlin.collections.HashMap

class SiteInformationResidentActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySiteInformationResidentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    companion object {
        private const val TAG = "SiteInformationResidentActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiteInformationResidentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        auth = Firebase.auth
        binding.backToResidentNewAccountActivityButton.setOnClickListener { backToResidentNewAccountActivityButtonClicked() }
        binding.signUpButton.setOnClickListener { signUpButtonClicked() }
    }

    private fun backToResidentNewAccountActivityButtonClicked() {
        val intent = Intent(this, ResidentNewAccountActivity::class.java)
        startActivity(intent)
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
        val blockNo = binding.blockNoText.text.toString()
        val flatNo = binding.flatNoText.text.toString().toInt()

        return hashMapOf(
            "siteName" to siteName,
            "city" to city,
            "district" to district,
            "blockNo" to blockNo,
            "flatNo" to flatNo,
        )
    }

    private fun isBlank(): Boolean {
        return binding.cityText.text.isBlank() || binding.countyText.text.isBlank() || binding.siteNameText.text.isBlank() ||
                binding.flatNoText.text.isBlank() || binding.blockNoText.text.isBlank()
    }

    private fun signUpButtonClicked() {
        if (isBlank()) {
            println("I am in the toast")
            Toast.makeText(
                this,
                "Lütfen boş alanları doldurunuz!!!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val user = getUserData()
            val site = getSiteData()

            val resident = hashMapOf(
                "fullName" to user["fullName"],
                "phoneNumber" to user["phoneNumber"],
                "email" to user["email"],
                "password" to user["password"],
                "typeOfUser" to "Sakin",
                "siteName" to site["siteName"],
                "city" to site["city"],
                "district" to site["district"],
                "blockNo" to site["blockNo"],
                "flatNo" to site["flatNo"],
                "debt" to 0.0
            )
            createResident(user, resident)
        }

    }

    private fun createResident(user: HashMap<String, String>,
                               resident: HashMap<String, Any?>) {
        auth.createUserWithEmailAndPassword(user["email"].toString(), user["password"].toString())
            .addOnSuccessListener {
                Log.d(TAG, "User successfully created!")
                resident["uid"] = auth.currentUser?.uid.toString()
                saveResidentIntoDB(resident, user["email"].toString())
                updateUserInfo(resident)
                val intent = Intent(this, HomePageResidentActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.w(TAG, "User couldn't be created", it)
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }


    private fun updateUserInfo(resident: HashMap<String, Any?>) {
        val currentUser = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = resident["fullName"].toString()
        }
        currentUser?.updateProfile(profileUpdates)
    }

    private fun saveResidentIntoDB(resident: HashMap<String, Any?>, email: String) {
        db.collection("residents").document(email)
            .set(resident)
            .addOnSuccessListener {
                Log.d(TAG, "Resident document successfully written!")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error writing resident info document", e)
            }
    }

}