package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsPasswordViewModel: ViewModel() {

    companion object {
        const val TAG = "SettingsPasswordViewModel"
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val previousPassword = MutableLiveData("")
    val newPassword = MutableLiveData("")
    val repeatedNewPassword = MutableLiveData("")

    private val _isChanged = MutableLiveData<Boolean?>()
    val isChanged: LiveData<Boolean?> get() = _isChanged

    private val _isAuthenticated = MutableLiveData<Boolean?>()
    val isAuthenticated: LiveData<Boolean?> get() = _isAuthenticated

    fun updatePassword() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser
            currentUser?.let {
                if (arePasswordSame()) {
                    newPassword.value?.let { password ->
                        it.updatePassword(password).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(TAG, "updatePassword -> SUCCESSFUL!")
                                _isChanged.postValue(true)
                            } else {
                                Log.d(TAG, "updatePassword -> UNSUCCESSFUL!")
                                _isChanged.postValue(false)
                            }
                        }
                    }
                }
            }
        } catch (e: FirebaseException) {
            Log.e(TAG, "updatePassword --> $e")
            _isChanged.postValue(null)
        }
    }

    private fun arePasswordSame() = newPassword.value == repeatedNewPassword.value
            && !newPassword.value.isNullOrEmpty() && !repeatedNewPassword.value.isNullOrEmpty()

    fun areEmptyOrNull() = newPassword.value.isNullOrEmpty() || !repeatedNewPassword.value.isNullOrEmpty()
            && previousPassword.value.isNullOrEmpty()

    fun reAuthenticateUser() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val user = auth.currentUser
            user?.let { currentUser ->
                previousPassword.value?.let { previousPassword ->
                    val credential = EmailAuthProvider.getCredential(user.email!!, previousPassword)
                    currentUser.reauthenticate(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "reAuthenticateUser -> SUCCESSFUL!")
                            _isAuthenticated.postValue(true)
                        } else {
                            Log.d(TAG, "reAuthenticateUser -> UNSUCCESSFUL!")
                            _isAuthenticated.postValue(false)
                        }
                    }
                }
            }
        } catch (e: FirebaseException) {
            Log.e(TAG, "reAuthenticateUser -> $e")
            _isAuthenticated.postValue(null)
        }
    }
}