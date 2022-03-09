package com.graduationproject.grad_project.view.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.graduationproject.grad_project.R

class HomePageAdminActivity : AppCompatActivity() {
    private lateinit var bottom_nav : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page_admin)
        val navController= findNavController(R.id.hostFragment)
        bottom_nav.setupWithNavController(navController)
    }
}