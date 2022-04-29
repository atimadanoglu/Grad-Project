package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.ServicesOperations
import com.graduationproject.grad_project.model.Service

class ServicesResidentViewModel: ViewModel() {

    private val _services = MutableLiveData<MutableList<Service?>>()
    val services: LiveData<MutableList<Service?>> get() = _services

    fun retrieveServices() {
        ServicesOperations.retrieveServicesForResident(_services)
    }

}