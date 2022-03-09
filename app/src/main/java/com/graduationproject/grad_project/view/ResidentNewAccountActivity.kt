package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityResidentNewAccountBinding

class ResidentNewAccountActivity : AppCompatActivity() {

    private lateinit var binding : ActivityResidentNewAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidentNewAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun backToChoosingUserPageButtonClicked(view: View) {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }
    fun goToSiteInformationResidentActivityPageClicked(view: View) {
        if (binding.TextEmailAddress.text.isEmpty() ||
            binding.TextPassword.text.isEmpty() ||
            binding.phoneNumberText.text.isEmpty() ||
            binding.fullNameText.text.isEmpty()) {
            Toast.makeText(this, "Lütfen gerekli tüm kısımları doldurunuz!!!", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(this, SiteInformationResidentActivity::class.java).apply {
                putExtra("fullName", binding.fullNameText.text.toString())
                putExtra("phoneNumber",binding.phoneNumberText.text.toString())
                putExtra("email",binding.TextEmailAddress.text.toString())
                putExtra("password", binding.TextPassword.text.toString())
            }
            startActivity(intent)
        }
    }
    fun goToLoginPageButtonClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}