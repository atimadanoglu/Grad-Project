package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.*

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
        scope: CoroutineDispatcher = Dispatchers.IO
    ): String {
        return withContext(scope) {
            setEmail(email)
            setPassword(password)
            takeTheUserType(email)
            UserOperations.loginWithEmailAndPassword(email, password)
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

}