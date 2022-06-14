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

    private val _clickedResident = MutableLiveData<SiteResident>()
    val clickedResident: LiveData<SiteResident> get() = _clickedResident

    private val _navigateToPhoneDial = MutableLiveData<Boolean?>()
    val navigateToPhoneDial: LiveData<Boolean?> get() = _navigateToPhoneDial
    private var _phoneNumber: String = ""
    val phoneNumber get() = _phoneNumber

    fun retrieveAwaitingResidents() {
        UserOperations.retrieveAwaitingResidents(_awaitingResidents)
    }

    fun saveClickedResident(resident: SiteResident) {
        _clickedResident.value = resident
    }

    fun navigateToPhoneDial(phoneNumber: String) {
        _phoneNumber = phoneNumber
        _navigateToPhoneDial.value = true
    }

    fun navigatedToPhoneDial() {
        _navigateToPhoneDial.value = null
    }

    fun acceptResident() {
        clickedResident.value?.let { UserOperations.acceptResident(it) }
    }

    fun rejectResident() {
        clickedResident.value?.let { UserOperations.rejectResident(it) }
    }
}