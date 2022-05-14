package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.UserOperations

class SettingsPhoneViewModel: ViewModel() {

    val phoneNumber = MutableLiveData("")

    private val _isSuccessful = MutableLiveData<Boolean?>()
    val isSuccessful: LiveData<Boolean?> get() = _isSuccessful

    fun updatePhoneNumber() {
        phoneNumber.value?.let { UserOperations.updatePhoneNumberForAdmin(it, _isSuccessful) }
    }

}