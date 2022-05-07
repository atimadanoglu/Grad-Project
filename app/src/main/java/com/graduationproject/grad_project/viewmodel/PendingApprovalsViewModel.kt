package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.SiteResident
import kotlinx.coroutines.launch

class PendingApprovalsViewModel: ViewModel() {

    private val _awaitingResidents = MutableLiveData<MutableList<SiteResident?>>()
    val awaitingResidents: LiveData<MutableList<SiteResident?>> get() = _awaitingResidents

    private val _navigateToPhoneDial = MutableLiveData<Boolean?>()
    val navigateToPhoneDial: LiveData<Boolean?> get() = _navigateToPhoneDial
    private var _phoneNumber: String = ""
    val phoneNumber get() = _phoneNumber

    fun retrieveAwaitingResidents() {
        viewModelScope.launch {
            UserOperations.retrieveAwaitingResidents(_awaitingResidents)
        }
    }

    fun navigateToPhoneDial(phoneNumber: String) {
        _phoneNumber = phoneNumber
        _navigateToPhoneDial.value = true
    }
}