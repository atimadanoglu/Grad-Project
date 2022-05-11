package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.UserOperations

class WaitingApprovalResidentViewModel: ViewModel() {

    private val _status = MutableLiveData("")
    val status: LiveData<String?> get() = _status

    fun checkStatus() {
        UserOperations.checkRegistrationStatus(_status)
    }

    fun navigated() {
        _status.value = null
    }
}