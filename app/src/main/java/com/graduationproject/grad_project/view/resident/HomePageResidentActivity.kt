package com.graduationproject.grad_project.view.resident

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.graduationproject.grad_project.databinding.ActivityHomePageResidentBinding

class HomePageResidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageResidentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageResidentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = binding.mainFragmentContainerViewForResident.getFragment() as NavHostFragment
/*
        val navHostFragmentForNavView = binding.secondNavHostFragmentForResident.getFragment() as NavHostFragment
*/
        val navController = navHostFragment.navController
/*
        val navControllerForNavView = navHostFragmentForNavView.navController
*/
/*
        binding.navigationView.setupWithNavController(navControllerForNavView)
*/
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setNavigationItemSelectedListener {
/*
            makeMainNavHostGone()
*/
            true
        }
        binding.bottomNavigation.setOnItemSelectedListener {
/*
            makeSecondNavHostGone()
*/
            true
        }
    }
/*
    private fun makeMainNavHostGone() {
        binding.mainFragmentContainerViewForResident.visibility = GONE
        binding.secondNavHostFragmentForResident.visibility = VISIBLE
    }

    private fun makeSecondNavHostGone() {
        binding.mainFragmentContainerViewForResident.visibility = VISIBLE
        binding.secondNavHostFragmentForResident.visibility = GONE
    }*/
}