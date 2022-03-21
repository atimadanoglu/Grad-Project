package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.graduationproject.grad_project.databinding.ActivitySiteInformationBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.onesignal.OneSignal
import java.util.*
import kotlin.collections.HashMap

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
            val user = getUserData()
            val site = getSiteData()

            val admin: HashMap<String, Any?> = hashMapOf(
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
            createUser(user, site, admin)
        }

    }

    private fun createUser(
        user: HashMap<String, String>,
        site: HashMap<String, Any>,
        admin: HashMap<String, Any?>
    ) {
        auth.createUserWithEmailAndPassword(user["email"].toString(), user["password"].toString())
            .addOnSuccessListener {
                val uid = it.user?.uid
                admin["uid"] = uid.toString()
                db.collection("sites")
                    .document("siteName:${site["siteName"]}-city:${site["city"]}-district:${site["district"]}").set(site)
                    .addOnSuccessListener {
                        Log.d(TAG, "Site document successfully written!")
                        savePlayerId(admin)
                        // Write admin info into DB
                        saveAdminIntoDB(admin, admin["email"].toString())
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Site document couldn't be written", exception)
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Log.w(TAG, "User couldn't be created", it)
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun savePlayerId(admin: HashMap<String, Any?>) {
        val uuid = UUID.randomUUID().toString()
        OneSignal.setExternalUserId(uuid)
        admin["player_id"] = uuid
    }

    private fun saveAdminIntoDB(admin: HashMap<String, Any?>, email: String) {
        db.collection("administrators")
            .document(email)
            .set(admin)
            .addOnSuccessListener {
                Log.d(TAG, "Administrator document successfully written!")
                val intent = Intent(this, HomePageAdminActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Yönetici oluşturulamadı!", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Error as writing admin info document", e)
            }
    }

    private fun backToAdminNewAccountActivityButtonClicked() {
        val intent = Intent(this, AdministratorNewAccountActivity::class.java)
        startActivity(intent)
    }
}