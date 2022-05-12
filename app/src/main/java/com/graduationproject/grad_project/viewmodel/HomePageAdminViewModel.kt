package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.launch

class HomePageAdminViewModel: ViewModel() {

    val siteName = MutableLiveData("")

    private val _isSignedOut = MutableLiveData<Boolean?>()
    val isSignedOut: LiveData<Boolean?> get() = _isSignedOut

    fun retrieveSiteName() = viewModelScope.launch {
        UserOperations.retrieveSiteNameForAdmin(siteName)
    }
    fun signOut() {
        UserOperations.signOut(_isSignedOut)
    }

}