package com.graduationproject.grad_project.view.resident

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageResidentBinding

class HomePageResidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageResidentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageResidentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerViewForResident) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }
}