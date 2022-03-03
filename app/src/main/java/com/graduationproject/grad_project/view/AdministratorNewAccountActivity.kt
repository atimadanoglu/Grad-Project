package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.graduationproject.grad_project.R

class AdministratorNewAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrator_new_account)
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
        val intent = Intent(this, SiteInformationActivity::class.java)
        startActivity(intent)
    }
}