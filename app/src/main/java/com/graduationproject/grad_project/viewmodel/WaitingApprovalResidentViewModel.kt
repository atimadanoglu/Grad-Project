package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WaitingApprovalResidentViewModel: ViewModel() {

    private val _isVerified = MutableLiveData<Boolean?>()
    val isVerified: LiveData<Boolean?> get() = _isVerified

    fun checkVerifiedStatus() {
        viewModelScope.launch {
            delay(5000L)
            val auth = FirebaseAuth.getInstance()
            auth.currentUser?.email?.let { UserOperations.checkVerifiedStatus(_isVerified, it) }
        }
    }

    fun clear() {
        _isVerified.value = false
    }
}