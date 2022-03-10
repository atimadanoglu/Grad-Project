package com.graduationproject.grad_project.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ActivityLoginBinding
import com.graduationproject.grad_project.databinding.FragmentAnnouncementsBinding
import com.graduationproject.grad_project.view.admin.AnnouncementsFragment
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var myAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var bindingLogin : FragmentAnnouncementsBinding

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        myAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = myAuth.currentUser
       /* if (currentUser != null) {
            //TODO
            val intent = Intent(this, HomePageAdminActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }

    fun signUpHereButtonClicked(view: View) {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }
    fun loginButtonClicked(view: View) {

        val email = binding.TextEmailAddress.text.toString()
        val password = binding.TextPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Lütfen gerekli alanları doldurunuz!!!", Toast.LENGTH_LONG)
                .show()
        } else {
            myAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "User successfully logged in!")
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage,Toast.LENGTH_LONG).show()
                    Log.w(TAG, "Sign-in Error!", it)
                }

            val currentUser = myAuth.currentUser?.uid
            db.collection("administrators").document(email)
                .get()
                .addOnSuccessListener {
                    if (it.get("typeOfUser") != null) {
                        Log.d(TAG, "Found a user with that email address on admin collection!")
                        val intent = Intent(this, HomePageAdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }.addOnFailureListener {
                    Log.w(TAG, "Error while searching a user with that email!!!", it)
                }

            db.collection("residents").document(email)
                .get()
                .addOnSuccessListener {
                    if (it.get("typeOfUser") != null) {
                        Log.d(TAG, "Found a user with that email address on resident collection!")
                        val intent = Intent(this, HomePageResidentActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    Log.w(TAG, "Error while searching a user with that email!!!", it)
                }

        }
    }
}