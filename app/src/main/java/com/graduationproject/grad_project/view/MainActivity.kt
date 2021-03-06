package com.graduationproject.grad_project.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment =
            binding.loginAndSignupNavigationNavHost.getFragment() as NavHostFragment
        val navController = navHostFragment.navController

        val bundle = intent.getStringExtra("fragmentName")
        if (bundle != null) {
            if (bundle == "rejectedFragment") {
                navController.navigate(R.id.action_loginFragment_to_errorDialogFragment)
            }
            if (bundle == "pendingApprovalFragment") {
                navController.navigate(R.id.action_loginFragment_to_waitingApprovalResidentFragment)
            }
        }
    }
}