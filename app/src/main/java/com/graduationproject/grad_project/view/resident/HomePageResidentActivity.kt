package com.graduationproject.grad_project.view.resident

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageResidentBinding
import com.graduationproject.grad_project.databinding.DrawerHeaderResidentBinding
import com.graduationproject.grad_project.view.MainActivity
import com.graduationproject.grad_project.viewmodel.HomePageResidentViewModel

class HomePageResidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageResidentBinding
    private val viewModel: HomePageResidentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageResidentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setDrawerHeader()
        val navHostFragment = binding.mainFragmentContainerViewForResident.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        viewModel.isSignedOut.observe(this) {
            it?.let {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)
    }

    private fun setDrawerHeader() {
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderResidentBinding.bind(header)
        drawerHeaderBinding.viewModel = viewModel
        viewModel.retrieveSiteName()
        drawerHeaderBinding.signOut.setOnClickListener {
            showAlertMessage()
        }
    }

    private fun showAlertMessage() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.eminMisiniz)
            .setPositiveButton(R.string.evet) { _, _ ->
                viewModel.signOut()
            }
            .setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }
}