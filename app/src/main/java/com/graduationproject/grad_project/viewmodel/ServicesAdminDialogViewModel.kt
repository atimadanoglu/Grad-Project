package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.ServicesOperations
import com.graduationproject.grad_project.model.Service

class ServicesAdminDialogViewModel: ViewModel() {

    private val _type = MutableLiveData("")
    val type: LiveData<String?> get() = _type
    private val _name = MutableLiveData("")
    val name: LiveData<String?> get() = _name
    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String?> get() = _phoneNumber

    fun setType(value: String) { _type.value = value }
    fun setName(value: String) { _name.value = value }
    fun setPhoneNumber(value: String) { _phoneNumber.value = value }

    fun addService(service: Service) {
        ServicesOperations.addService(service)
    }

    fun areNull(): Boolean {
        return _name.value == null && _phoneNumber.value == null && _type.value == null
    }

}