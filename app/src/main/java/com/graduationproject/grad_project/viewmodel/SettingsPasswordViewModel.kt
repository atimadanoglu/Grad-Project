package com.graduationproject.grad_project.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingsPasswordViewModel: ViewModel() {

    companion object {
        const val TAG = "SettingsPasswordViewModel"
    }
    private val _previousPassword = MutableLiveData("")
    val previousPassword: LiveData<String> get() = _previousPassword
    private val _newPassword = MutableLiveData("")
    val newPassword: LiveData<String> get() = _newPassword
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> get() = _isChanged

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    fun setPreviousPassword(previousPassword: String) { _previousPassword.value = previousPassword }
    fun setNewPassword(newPassword: String) { _newPassword.value = newPassword }

    private fun updatePassword(password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.currentUser?.updatePassword(password)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "updatePassword --> Updating password is SUCCESSFUL!")
                        launch {
                            withContext(Dispatchers.Main) {
                                _isChanged.value = true
                            }
                        }
                    }
                }?.await()
            } catch (e: Exception) {
                Log.e(TAG, "updatePassword ---> $e")
                launch {
                    withContext(Dispatchers.Main) {
                        _isChanged.value = false
                    }
                }
            }
        }
    }

    fun reAuthenticateAndUpdatePassword(password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = auth.currentUser?.email
                val credential = email?.let {
                    EmailAuthProvider.getCredential(
                        it, password
                    )
                }
                auth.signOut()
                email?.let { userEmail ->
                    _newPassword.value?.let { newPassword ->
                        auth.signInWithEmailAndPassword(userEmail, newPassword).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(TAG, "reAuthenticateUser --> ReAuthentication is successful!")
                                launch(Dispatchers.Main) {
                                    _isAuthenticated.value = true
                                }
                                _newPassword.value?.let { it1 -> updatePassword(it1) }
                            }
                        }.await()
                } }

            } catch (e: Exception) {
                Log.e(TAG, "reAuthenticateUser --> $e")
                launch(Dispatchers.Main) {
                    _isAuthenticated.value = false
                }
            }
        }
    }
}