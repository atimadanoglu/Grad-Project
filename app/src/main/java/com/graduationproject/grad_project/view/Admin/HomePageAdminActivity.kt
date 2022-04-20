package com.graduationproject.grad_project.view.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding
import com.graduationproject.grad_project.databinding.DrawerHeaderAdminBinding
import com.graduationproject.grad_project.view.MainActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePageAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomePageAdminBinding
    private lateinit var auth : FirebaseAuth

    companion object {
        private const val TAG = "HomePageAdminActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        val header = binding.navigationView.getHeaderView(0)
        val drawerHeaderBinding = DrawerHeaderAdminBinding.bind(header)
        setDisplayName(drawerHeaderBinding)

        val navHostFragment =
            binding.mainFragmentContainerView.getFragment() as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)

        drawerHeaderBinding.signOut.setOnClickListener {
            showAlertMessage()
        }
    }

    private fun showAlertMessage() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.eminMisiniz)
            .setPositiveButton(R.string.evet) { _, _ ->
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.setNegativeButton(R.string.hayır) { _, _ -> }
            .create().show()
    }

    private fun setDisplayName(drawerHeaderBinding: DrawerHeaderAdminBinding) {
        lifecycleScope.launch {
            val a = async {
                if (auth.currentUser?.displayName.isNullOrEmpty()) {
                    delay(1500L)
                    if (auth.currentUser?.displayName == null) {
                        delay(1500L)
                    }
                    auth.currentUser?.displayName?.let {
                        drawerHeaderBinding.headerAccountName.text = it
                    }
                }
            }
            a.await()
        }
    }
}