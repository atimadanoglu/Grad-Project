package com.graduationproject.grad_project.view.resident

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageResidentBinding
import com.graduationproject.grad_project.databinding.DrawerHeaderBinding

class HomePageResidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageResidentBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageResidentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        val navHostFragment = binding.mainFragmentContainerViewForResident.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)
        auth.currentUser?.let { setHeader(it) }
    }

    private fun setHeader(currentUser: FirebaseUser) {
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderBinding.bind(header)

        drawerHeaderBinding.headerAccountName.text = currentUser.displayName
        drawerHeaderBinding.headerAccountType.setText(R.string.sakin)
        drawerHeaderBinding.headerEmailAddress.text = currentUser.email
    }


}