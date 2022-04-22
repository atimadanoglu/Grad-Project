package com.graduationproject.grad_project.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

class SettingsPasswordViewModel: ViewModel() {

    private val _previousPassword = MutableLiveData("")
    val previousPassword: LiveData<String> get() = _previousPassword
    private val _newPassword = MutableLiveData("")
    val newPassword: LiveData<String> get() = _newPassword

    fun setPreviousPassword(previousPassword: String) { _previousPassword.value = previousPassword }
    fun setNewPassword(newPassword: String) { _newPassword.value = newPassword }

    fun updatePassword() {
        viewModelScope.launch {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let { currentUserEmail ->
                _newPassword.value?.let { newPassword ->
                    UserOperations.updatePassword(newPassword)
                    UserOperations.reAuthenticateUser(currentUserEmail, newPassword)
                }
            }
        }
    }

    fun checkPreviousAndNewPassword() {

    }

}