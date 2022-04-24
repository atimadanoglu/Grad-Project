package com.graduationproject.grad_project.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityOpenScreenBinding
import android.util.Pair as UtilPair

class OpenScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenScreenBinding
    private val splashScreen = 4000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        val top_animation = AnimationUtils.loadAnimation(this, R.anim.top_animation)

        binding.mainIcon.animation = top_animation
        binding.mainText.animation = bottom_animation
        binding.appTypeText.animation = bottom_animation

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                UtilPair.create(binding.mainIcon, "logo_image"),
                UtilPair.create(binding.mainText, "logo_text"))
            startActivity(intent, options.toBundle())
        },splashScreen.toLong())
    }
}