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
import com.graduationproject.grad_project.databinding.ActivitySiteInformationResidentBinding
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import java.io.Serializable

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
    }

    fun backToResidentNewAccountActivityButtonClicked(view: View) {
        val intent = Intent(this, ResidentNewAccountActivity::class.java)
        startActivity(intent)
    }

    fun signUpButtonClicked(view: View) {

        if (binding.cityText.text.isBlank() || binding.countyText.text.isBlank() || binding.siteNameText.text.isBlank() ||
            binding.flatNoText.text.isBlank() || binding.blockNoText.text.isBlank()
        ) {
            println("I am in the toast")
            Toast.makeText(
                this,
                "Lütfen gerekli tüm kısımları doldurunuz!!!",
                Toast.LENGTH_SHORT
            ).show()
        } else {

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

            val city = binding.cityText.text.toString()
            val district = binding.countyText.text.toString()
            val siteName = binding.siteNameText.text.toString()
            val blockNo = binding.blockNoText.text.toString()
            val flatNo = binding.flatNoText.text.toString().toInt()

            val resident = hashMapOf(
                "fullName" to fullName,
                "phoneNumber" to phoneNumber,
                "email" to email,
                "password" to password,
                "typeOfUser" to "Resident",
                "siteName" to siteName,
                "city" to city,
                "district" to district,
                "blockNo" to blockNo,
                "flatNo" to flatNo
            )

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                Log.d(TAG, "User successfully created!")
                resident["uid"] = auth.currentUser?.uid.toString()

                db.collection("residents").document(email)
                    .set(resident)
                    .addOnSuccessListener {
                        Log.d(TAG, "Resident document successfully written!")
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error writing resident info document", e)
                    }
                val intent = Intent(this, HomePageResidentActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.w(TAG, "User couldn't be created", it)
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }









    }
}