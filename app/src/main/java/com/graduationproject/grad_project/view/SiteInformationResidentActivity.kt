package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class SiteInformationResidentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_information_resident)
    }

    fun backToResidentNewAccountActivityButtonClicked(view: View) {
        val intent = Intent(this, ResidentNewAccountActivity::class.java)
        startActivity(intent)
    }
    fun signUpButtonClicked(view: View) {}
}