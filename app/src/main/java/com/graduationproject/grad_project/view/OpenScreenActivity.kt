package com.graduationproject.grad_project.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityOpenScreenBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import com.graduationproject.grad_project.viewmodel.OpenScreenViewModel
import android.util.Pair as UtilPair

class OpenScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenScreenBinding
    private val viewModel: OpenScreenViewModel by viewModels()
    private val splashScreen = 3000
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)

        binding.mainIcon.animation = topAnimation
        binding.mainText.animation = bottomAnimation
        binding.appTypeText.animation = bottomAnimation

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                UtilPair.create(binding.mainIcon, "logo_image"),
                UtilPair.create(binding.mainText, "logo_text"))
            viewModel.checkSignedInStatus()
            observe(intent, options)
        },splashScreen.toLong())
    }

    private fun observe(intent: Intent, options: ActivityOptions) {
        viewModel.isSignedIn.observe(this) {
            it?.let {
                if (it) {
                    viewModel.isResident()
                    viewModel.isAdmin()
                } else {
                    startActivity(intent, options.toBundle())
                }
            }
        }
        viewModel.isAdmin.observe(this) {
            it?.let {
                if (it) {
                    if (viewModel.goToAdminHomePageRule()) {
                        goToAdminHomePageActivity()
                    }
                }
            }
        }
        viewModel.isResident.observe(this) {
            it?.let {
                if (it) {
                    viewModel.checkStatus()
                }
            }
        }
        viewModel.registrationStatus.observe(this) {
            if (!it.isNullOrEmpty()) {
                if (viewModel.goToResidentHomePageRule()) {
                    goToResidentHomePageActivity()
                }
                if (viewModel.goToRejectedResidentPageRule()) {
                    intent.putExtra("fragmentName", "rejectedFragment")
                    startActivity(intent, options.toBundle())
                }
                if (viewModel.goToPendingApprovalPageRule()) {
                    intent.putExtra("fragmentName", "pendingApprovalFragment")
                    startActivity(intent, options.toBundle())
                }
            }
        }
    }

    private fun goToResidentHomePageActivity() {
        val intent = Intent(this, HomePageResidentActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToAdminHomePageActivity() {
        val intent = Intent(this, HomePageAdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}