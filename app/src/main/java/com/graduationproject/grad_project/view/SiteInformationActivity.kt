package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class SiteInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_information)
    }

    fun signUpButtonClicked(view: View) {}
    fun backToAdminNewAccountActivityButtonClicked(view: View) {
        val intent = Intent(this, AdministratorNewAccountActivity::class.java)
        startActivity(intent)
    }
}