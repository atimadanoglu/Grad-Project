package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.RegistrationStatus
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val _isAdmin = MutableLiveData<Boolean?>()
    val isAdmin: LiveData<Boolean?> get() = _isAdmin

    private val _isResident = MutableLiveData<Boolean?>()
    val isResident: LiveData<Boolean?> get() = _isResident

    private val _isSignedIn = MutableLiveData(false)
    val isSignedIn: LiveData<Boolean?> get() = _isSignedIn

    private val _registrationStatus = MutableLiveData("")
    val registrationStatus: LiveData<String?> get() = _registrationStatus

    fun login() {
        if (!isNullOrEmpty()) {
            UserOperations.login(email.value!!, password.value!!, _isSignedIn)
        }
    }

    fun isNullOrEmpty() = email.value.isNullOrEmpty() && password.value.isNullOrBlank()

    fun isAdmin() = viewModelScope.launch {
        if (!isNullOrEmpty()) {
            UserOperations.isAdmin(email.value.toString(), _isAdmin)
        }
    }
    fun isResident() = viewModelScope.launch {
        if (!isNullOrEmpty()) {
            UserOperations.isResident(email.value.toString(), _isResident)
        }
    }

    fun goToAdminHomePageRule() = _isAdmin.value == true && _isSignedIn.value == true
    fun goToResidentHomePageRule() = _isResident.value == true && _isSignedIn.value == true
            && _registrationStatus.value == RegistrationStatus.VERIFIED
    fun goToRejectedResidentPageRule() = _isResident.value == true && _isSignedIn.value == true
            && _registrationStatus.value == RegistrationStatus.REJECTED
    fun goToPendingApprovalPageRule() = _isResident.value == true && _isSignedIn.value == true
            && _registrationStatus.value == RegistrationStatus.PENDING

    fun checkStatus() = viewModelScope.launch {
        UserOperations.checkRegistrationStatus(_registrationStatus)
    }
}