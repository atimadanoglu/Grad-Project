package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.UserOperations

class HomePageResidentViewModel: ViewModel() {

    private val _siteName = MutableLiveData("")
    val siteName: LiveData<String?> get() = _siteName

    private val _isSignedOut = MutableLiveData<Boolean?>()
    val isSignedOut: LiveData<Boolean?> get() = _isSignedOut

    fun retrieveSiteName() {
        UserOperations.retrieveSiteNameForResident(_siteName)
    }

    fun signOut() {
        UserOperations.signOut(_isSignedOut)
    }

}