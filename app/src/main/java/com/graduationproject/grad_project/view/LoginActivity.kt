package com.graduationproject.grad_project.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.databinding.ActivityLoginBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var myAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var adminRef: CollectionReference
    private lateinit var residentRef: CollectionReference

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
        adminRef = db.collection("administrators")
        residentRef = db.collection("residents")
        checkUserType()
        binding.LogIn.setOnClickListener {
            lifecycleScope.launch {
                loginButtonClicked()
            }
        }
        binding.signUpHereTextButton.setOnClickListener { signUpHereButtonClicked() }
    }

    // Check that if there is any admin user in admin collection with that email
    private fun checkAdminCollection(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = db.collection("administrators").document(email)
                    .get()
                    .await()
                println("${user["typeOfUser"]}")
                if (user["typeOfUser"] == "Yönetici") {
                    println("yönetici içreisindeyim")
                    goToAdminHomePage()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun checkResidentsCollection(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = db.collection("residents").document(email)
                    .get()
                    .await()
                println("${user["typeOfUser"]}")

                if (user["typeOfUser"] == "Sakin") {
                    println("sakin içerisindeyim")
                    goToResidentHomePage()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun goToAdminHomePage() {
        val intent = Intent(this, HomePageAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToResidentHomePage() {
        val intent = Intent(this, HomePageResidentActivity::class.java)
        startActivity(intent)
        finish()
    }


    // Check whether user is an admin or a resident
    // According to that, direct the users their page
    private fun checkUserType() {

        myAuth.signOut()

        val currentUser = myAuth.currentUser
        if (currentUser != null) {
            lifecycleScope.launch {
                try {
                    val admin = db.collection("administrators")
                        .document(currentUser.email.toString())
                        .get().await()
                    admin?.let {
                        if (it["typeOfUser"] == "Yönetici") {
                            goToAdminHomePage()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
                launch {
                    try {
                        val resident = db.collection("residents")
                            .document(currentUser.email.toString())
                            .get().await()
                        resident?.let {
                            if (it["typeOfUser"] == "Sakin") {
                                goToResidentHomePage()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
        } else {
            Log.d(TAG, "There is no signed-in user!!!")
        }
    }

    private fun signUpHereButtonClicked() {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }

    private suspend fun loginButtonClicked() {
        val email = "asd"
        //val email = binding.TextEmailAddress.text.toString()
        val password = binding.TextPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Lütfen gerekli alanları doldurunuz!!!", Toast.LENGTH_LONG)
                .show()
        } else {
            try {
                signIn(email, password).await()
                checkAdminCollection(email)
                checkResidentsCollection(email)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun signIn(email: String, password: String): Task<AuthResult> {
        return myAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                println("güncelledi")
                it.user?.let { it1 -> myAuth.updateCurrentUser(it1) }
            }
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
}