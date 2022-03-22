package com.graduationproject.grad_project.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.databinding.ActivityLoginBinding
import com.graduationproject.grad_project.view.admin.HomePageAdminActivity
import com.graduationproject.grad_project.view.resident.HomePageResidentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
        binding.LogIn.setOnClickListener { loginButtonClicked() }
        binding.signUpHereTextButton.setOnClickListener { signUpHereButtonClicked() }
    }

    // Check that if there is any admin user in admin collection with that email
    private fun checkAdminCollection(email: String): Task<DocumentSnapshot> {
        return db.collection("administrators")
            .document(email)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Found a user with that email address on admin collection!")
                if (it["typeOfUser"] == "Yönetici") {
                    val intent = Intent(this, HomePageAdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Log.w(TAG, "There is no user on admin collection!!", it)
            }
    }

    private fun checkResidentsCollection(email: String): Task<DocumentSnapshot> {
        return db.collection("residents").document(email)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Found a user with that email address in resident collection!")
                if (it["typeOfUser"] == "Sakin") {
                    val intent = Intent(this, HomePageResidentActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Log.w(TAG, "There is no user in resident collection", it)
            }
    }

    // Check whether user is an admin or a resident
    // According to that, direct the users their page
    private fun checkUserType() {
        val currentUser = myAuth.currentUser
        if (currentUser != null) {
            db.collection("administrators")
                .document(currentUser.email.toString())
                .get()
                .addOnSuccessListener { admin ->
                    if (admin["typeOfUser"] == "Yönetici") {
                        val intent = Intent(this, HomePageAdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    Log.w(TAG, "While checking user type, we couldn't find any admin!")
                }
            db.collection("residents")
                .document(currentUser.email.toString())
                .get()
                .addOnSuccessListener {
                    if (it["typeOfUser"] == "Sakin") {
                        val intent = Intent(this, HomePageResidentActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    Log.w(TAG, "While checking user type, we couldn't find any resident!")
                }
        } else {
            println("there is no signed-in user")
        }
    }

    private fun signUpHereButtonClicked() {
        val intent = Intent(this, SignUpMainActivity::class.java)
        startActivity(intent)
    }

    private fun loginButtonClicked() {

        val email = binding.TextEmailAddress.text.toString()
        val password = binding.TextPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Lütfen gerekli alanları doldurunuz!!!", Toast.LENGTH_LONG)
                .show()
        } else {
            signIn(email, password).onSuccessTask {
                checkAdminCollection(email)
                checkResidentsCollection(email)
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