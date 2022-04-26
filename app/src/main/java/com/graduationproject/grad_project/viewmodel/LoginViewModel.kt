package com.graduationproject.grad_project.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class LoginViewModel: ViewModel() {

    private var _email = ""
    val email get() = _email

    private var _password = ""
    val password get() = _password

    private var _isSignedIn = false
    val isSignedIn get() = _isSignedIn

    private var _typeOfUser = ""
    val typeOfUser get() = _typeOfUser

    fun setEmail(email: String) { _email = email }
    fun setPassword(password: String) { _password = password }
    fun setIsSignedIn(isSignedIn: Boolean) { _isSignedIn = isSignedIn }
    fun setTypeOfUser(userType: String) { _typeOfUser = userType }

    suspend fun makeLoginOperation(
        email: String,
        password: String,
        view: View?,
        scope: CoroutineDispatcher = Dispatchers.IO
    ): String {
        return withContext(scope) {
            UserOperations.loginWithEmailAndPassword(email, password, view)
            takeTheUserType(email)
            typeOfUser
        }
    }

    suspend fun takeTheUserType(email: String, scope: CoroutineDispatcher = Dispatchers.IO): String? {
        return withContext(scope) {
            try {
                _typeOfUser = UserOperations.takeTheUserType(email)
                typeOfUser
            } catch (e: Exception) {
                null
            }
        }
    }
    suspend fun isVerified() = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        println("current user is : ${auth.currentUser?.email}")
        auth.currentUser?.email?.let {
            val resident = db.collection("residents")
                .document(it)
                .get()
                .await()
            return@withContext resident["isVerified"].toString().toBoolean()
        }
    }

}