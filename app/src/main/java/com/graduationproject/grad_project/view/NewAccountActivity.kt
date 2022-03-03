package com.graduationproject.grad_project.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class NewAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_account)
    }

    fun backToChoosingUserPageButtonClicked(view: View) {}
    fun goToLoginPageButtonClicked(view: View) {}
}