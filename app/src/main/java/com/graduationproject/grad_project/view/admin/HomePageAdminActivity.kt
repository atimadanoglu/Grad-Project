package com.graduationproject.grad_project.view.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityHomePageAdminBinding
import com.graduationproject.grad_project.view.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomePageAdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityHomePageAdminBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser?.uid
        var userName = ""

        if (user != null) {
            db.collection("residents")
                .whereEqualTo("uid", user)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        userName = document.get("fullName") as String
                    }
                    binding.accountName.text = userName
                }
        }


    }
}