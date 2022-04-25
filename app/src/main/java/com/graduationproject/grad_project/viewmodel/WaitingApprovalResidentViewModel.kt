package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WaitingApprovalResidentViewModel: ViewModel() {

    private val _isVerified = MutableLiveData(false)
    val isVerified: LiveData<Boolean> get() = _isVerified

    fun checkVerifiedStatus() {
        viewModelScope.launch {
            delay(3000L)
            UserOperations.checkVerifiedStatus(_isVerified)
        }
    }

}