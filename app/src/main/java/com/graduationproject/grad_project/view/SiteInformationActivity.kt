package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.graduationproject.grad_project.databinding.ActivitySiteInformationBinding
import com.graduationproject.grad_project.model.Administrator
import com.graduationproject.grad_project.view.Admin.HomePageAdminActivity

class SiteInformationActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySiteInformationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth

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
    }

    fun signUpButtonClicked(view: View) {
        val i = intent.extras
        if (i != null) {
            val fullName = i.getString("fullName")
            val phoneNumber = i.getString("phoneNumber")
            val email = i.getString("email")
            val password = i.getString("password")
            val city = binding.cityText.text.toString()
            val district = binding.countyText.text.toString()
            val siteName = binding.siteNameText.text.toString()
            val blockCount = binding.blockCountText.text.toString().toInt()
            val flatCount = binding.flatCountText.text.toString().toInt()

            val admin = hashMapOf(
                "fullName" to fullName,
                "email" to email,
                "phoneNumber" to phoneNumber,
                "password" to password,
                "adminKey" to "abcdef"
            )

            val site = hashMapOf(
                "siteName" to siteName,
                "city" to city,
                "district" to district,
                "blockCount" to blockCount,
                "flatCount" to flatCount,
            )

            if (city.isEmpty() || district.isEmpty() || siteName.isEmpty() ||
                binding.blockCountText.text.isEmpty() || binding.flatCountText.text.isEmpty()
            ) {
                Toast.makeText(this, "Lütfen gerekli tüm kısımları doldurunuz!!!", Toast.LENGTH_LONG).show()
            } else {
                auth.createUserWithEmailAndPassword(email!!, password!!).addOnSuccessListener {
                    admin["uid"] = auth.currentUser?.uid
                    db.collection("sites").document("siteName:$siteName-city:$city-district:$district").set(site)
                        .addOnSuccessListener {
                            Log.d(TAG, "Site document successfully written!")
                        }
                        .addOnFailureListener {
                            Log.w(TAG, "Site document couldn't be written", it)
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                    val intent = Intent(this, HomePageAdminActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    Log.w(TAG, "User couldn't be created", it)
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
                // added admin collection into last document
                if (fullName != null) {
                    db.collection("sites").document("siteName:$siteName-city:$city-district:$district")
                        .collection("administrator").document("adminName:$fullName").set(admin)
                        .addOnSuccessListener {
                            Log.d(TAG, "Administrator document successfully written!")
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Error writing document", e)
                        }
                }

            }
        }

    }

    fun backToAdminNewAccountActivityButtonClicked(view: View) {
        val intent = Intent(this, AdministratorNewAccountActivity::class.java)
        startActivity(intent)
    }
}