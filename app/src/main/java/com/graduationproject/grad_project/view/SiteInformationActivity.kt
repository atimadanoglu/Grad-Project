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
        if (binding.cityText.text.isBlank() || binding.countyText.text.isBlank() || binding.siteNameText.text.isBlank() ||
            binding.flatCountText.text.isBlank() || binding.blockCountText.text.isBlank()
        ) {
            Toast.makeText(this, "Lütfen gerekli tüm kısımları doldurunuz!!!", Toast.LENGTH_LONG).show()
        } else {
            val i = intent.extras
            var fullName = ""
            var phoneNumber = ""
            var email = ""
            var password = ""
            val admin = hashMapOf(
                "fullName" to "",
                "phoneNumber" to "",
                "email" to "",
                "password" to "",
                "uid" to ""
            )

            if (i != null) {
                println("intent içindeyim")
                fullName = i.getString("fullName").toString()
                phoneNumber = i.getString("phoneNumber").toString()
                email = i.getString("email").toString()
                password = i.getString("password").toString()
                admin["fullName"] = fullName
                admin["phoneNumber"] = phoneNumber
                admin["email"] = email
                admin["password"] = password
            }

            val city = binding.cityText.text.toString()
            val district = binding.countyText.text.toString()
            val siteName = binding.siteNameText.text.toString()
            val blockCount = binding.blockCountText.text.toString().toInt()
            val flatCount = binding.flatCountText.text.toString().toInt()

            val site = hashMapOf(
                "siteName" to siteName,
                "city" to city,
                "district" to district,
                "blockCount" to blockCount,
                "flatCount" to flatCount,
            )

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                db.collection("sites")
                    .document("siteName:$siteName-city:$city-district:$district").set(site)
                    .addOnSuccessListener {
                        Log.d(TAG, "Site document successfully written!")
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Site document couldn't be written", it)
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
//TODO
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.w(TAG, "User couldn't be created", it)
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

            val uid = auth.currentUser?.uid
            admin.replace("uid", uid.toString())
            println(admin["uid"])

            db.collection("sites").document("siteName:$siteName-city:$city-district:$district")
                .collection("admin").document("admin").set(admin)
                .addOnSuccessListener {
                    Log.d(TAG, "Administrator document successfully written!")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }


        }

    }

    fun backToAdminNewAccountActivityButtonClicked(view: View) {
        val intent = Intent(this, AdministratorNewAccountActivity::class.java)
        startActivity(intent)
    }
}