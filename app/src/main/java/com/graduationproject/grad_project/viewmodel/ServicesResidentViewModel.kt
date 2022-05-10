package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.ServicesOperations
import com.graduationproject.grad_project.model.Service

class ServicesResidentViewModel: ViewModel() {

    private val _services = MutableLiveData<MutableList<Service?>>()
    val services: LiveData<MutableList<Service?>> get() = _services

    private val _navigateToPhoneDial = MutableLiveData<Boolean?>()
    val navigateToPhoneDial: LiveData<Boolean?> get() = _navigateToPhoneDial
    private var _phoneNumber: String = ""
    val phoneNumber get() = _phoneNumber

    fun retrieveServices() {
        ServicesOperations.retrieveServicesForResident(_services)
    }

    fun navigateToPhoneDial(phoneNumber: String) {
        _phoneNumber = phoneNumber
        _navigateToPhoneDial.value = true
    }
    fun navigatedPhoneDial() {
        _navigateToPhoneDial.value = null
    }
}