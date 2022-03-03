package com.graduationproject.grad_project.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class SignUpMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_main)
    }

    fun backToLoginPageButtonClicked(view: View) {}
    fun goToAdminNewAccountPageButtonClicked(view: View) {}
    fun goToResidentNewAccountPageButtonClicked(view: View) {}
}