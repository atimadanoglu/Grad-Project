package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.launch

class HomePageResidentViewModel: ViewModel() {
    val siteName = MutableLiveData("")
    val userName = MutableLiveData("")

    private val _isSignedOut = MutableLiveData<Boolean?>()
    val isSignedOut: LiveData<Boolean?> get() = _isSignedOut

    fun retrieveUserNameAndSiteName() = viewModelScope.launch {
        UserOperations.retrieveSiteNameAndUserNameForResident(
            siteName, userName
        )
    }
    fun signOut() {
        UserOperations.signOut(_isSignedOut)
    }
}