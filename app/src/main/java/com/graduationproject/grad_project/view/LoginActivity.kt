package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun signUpHereButtonClicked(view: View) {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }
    fun loginButtonClicked(view: View) {}
}