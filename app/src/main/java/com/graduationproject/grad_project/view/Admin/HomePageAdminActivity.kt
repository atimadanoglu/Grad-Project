package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding
import com.graduationproject.grad_project.databinding.DrawerHeaderAdminBinding
import com.graduationproject.grad_project.view.MainActivity
import com.graduationproject.grad_project.viewmodel.HomePageAdminViewModel

class HomePageAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageAdminBinding
    private val viewModel: HomePageAdminViewModel by viewModels()

    companion object {
        private const val TAG = "HomePageAdminActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment =
            binding.mainFragmentContainerView.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        setDrawerHeader()
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)
        viewModel.isSignedOut.observe(this) {
            it?.let {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setDrawerHeader() {
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderAdminBinding.bind(header)
        drawerHeaderBinding.viewModel = viewModel
        drawerHeaderBinding.lifecycleOwner = this
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
            }.setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }
}