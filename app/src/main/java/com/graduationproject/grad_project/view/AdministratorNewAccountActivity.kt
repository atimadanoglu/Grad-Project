package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityAdministratorNewAccountBinding

class AdministratorNewAccountActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdministratorNewAccountBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdministratorNewAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
    }

    fun backToChoosingUserPageButtonClicked(view: View) {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }
    fun goToLoginPageButtonClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    fun goToSiteInformationActivityPageClicked(view: View) {
        if (binding.TextEmailAddress.text.isEmpty() ||
            binding.TextPassword.text.isEmpty() ||
            binding.phoneNumberText.text.isEmpty() ||
            binding.fullNameText.text.isEmpty()) {
            Toast.makeText(this, "Lütfen gerekli tüm kısımları doldurunuz!!!", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(this, SiteInformationActivity::class.java).apply {
                putExtra("fullName", binding.fullNameText.text.toString())
                putExtra("phoneNumber",binding.phoneNumberText.text.toString())
                putExtra("email",binding.TextEmailAddress.text.toString())
                putExtra("password", binding.TextPassword.text.toString())
            }
            startActivity(intent)
        }
    }
}