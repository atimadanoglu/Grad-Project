package com.graduationproject.grad_project.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityOpenScreenBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import com.graduationproject.grad_project.viewmodel.OpenScreenViewModel
import kotlinx.coroutines.*
import android.util.Pair as UtilPair

class OpenScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenScreenBinding
    private val viewModel: OpenScreenViewModel by viewModels()
    private val splashScreen = 4000
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)

        binding.mainIcon.animation = topAnimation
        binding.mainText.animation = bottomAnimation
        binding.appTypeText.animation = bottomAnimation

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                UtilPair.create(binding.mainIcon, "logo_image"),
                UtilPair.create(binding.mainText, "logo_text"))
            if (auth.currentUser != null) {
                viewModel.setIsSignedIn(true)
                viewModel.setEmail(auth.currentUser!!.email.toString())
            }
            checkIfThereIsAnyUserSignedInAndDirectToPage(intent, options)
        },splashScreen.toLong())
    }

    private fun checkIfThereIsAnyUserSignedInAndDirectToPage(
        intent: Intent,
        options: ActivityOptions?
    ) {
        lifecycleScope.launch {
            val userType = async {
                viewModel.takeTheUserType(viewModel.email)
            }
            userType.await()?.let { viewModel.setTypeOfUser(it) }
            if (viewModel.isSignedIn && userType.await() == "Yönetici") {
                goToAdminHomePageActivity()
            }
            if (viewModel.isSignedIn && userType.await() == "Sakin") {
                goToResidentHomePageActivity()
            }
            if (!viewModel.isSignedIn) {
                startActivity(intent, options?.toBundle())
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