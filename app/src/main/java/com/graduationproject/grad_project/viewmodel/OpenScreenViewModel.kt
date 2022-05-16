package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.RegistrationStatus
import kotlinx.coroutines.launch

class OpenScreenViewModel: ViewModel() {
    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _isAdmin = MutableLiveData<Boolean?>()
    val isAdmin: LiveData<Boolean?> get() = _isAdmin

    private val _isResident = MutableLiveData<Boolean?>()
    val isResident: LiveData<Boolean?> get() = _isResident

    private val _isSignedIn = MutableLiveData<Boolean?>()
    val isSignedIn: LiveData<Boolean?> get() = _isSignedIn

    private val _registrationStatus = MutableLiveData("")
    val registrationStatus: LiveData<String?> get() = _registrationStatus

    fun checkSignedInStatus() {
        _isSignedIn.value = auth.currentUser != null
    }

    fun isNullOrEmpty() = email.value.isNullOrEmpty() && password.value.isNullOrBlank()

    fun isAdmin() = viewModelScope.launch {
        auth.currentUser?.email?.let { UserOperations.isAdmin(it, _isAdmin) }
    }
    fun isResident() = viewModelScope.launch {
        auth.currentUser?.email?.let { UserOperations.isResident(it, _isResident) }
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