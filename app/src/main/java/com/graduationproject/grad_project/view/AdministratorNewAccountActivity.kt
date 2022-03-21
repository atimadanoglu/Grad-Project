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
        binding.backToResidentNewAccountActivityButton.setOnClickListener { backToChoosingUserPageButtonClicked() }
        binding.nextButton.setOnClickListener { goToSiteInformationActivityPageClicked() }
        binding.goToLoginPageButton.setOnClickListener { goToLoginPageButtonClicked() }
    }

    private fun backToChoosingUserPageButtonClicked() {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }

    private fun goToLoginPageButtonClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun isBlank(): Boolean {
        return binding.TextEmailAddress.text.isBlank() ||
                binding.TextPassword.text.isBlank() ||
                binding.phoneNumberText.text.isBlank() ||
                binding.fullNameText.text.isBlank()
    }

    private fun isAdminKeyCorrect(): Boolean {
        return binding.ActivationKey.text.toString() == "qwerty"
    }

    private fun goToSiteInformationActivityPageClicked() {
        if (!isAdminKeyCorrect()) {
            Toast.makeText(this, "Yönetici aktivasyon anahtarını doğru girin!", Toast.LENGTH_LONG).show()
        }
        if (isBlank()) {
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