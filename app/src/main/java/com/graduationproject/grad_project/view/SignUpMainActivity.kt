package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class SignUpMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_main)
    }

    fun backToLoginPageButtonClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    fun goToAdminNewAccountPageButtonClicked(view: View) {
        val intent = Intent(this, AdministratorNewAccountActivity::class.java)
        startActivity(intent)
    }
    fun goToResidentNewAccountPageButtonClicked(view: View) {
        val intent = Intent(this, ResidentNewAccountActivity::class.java)
        startActivity(intent)
    }
}