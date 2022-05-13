package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.launch

class SettingsResidentViewModel: ViewModel() {

    private val _fullName = MutableLiveData("")
    val fullName: LiveData<String> get() = _fullName
    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String> get() = _phoneNumber
    private val _email = MutableLiveData("")
    val email: LiveData<String> get() = _email
    private val _password = MutableLiveData("*********")
    val password: LiveData<String> get() = _password

    fun getResident() {
        viewModelScope.launch {
            val residentEmail = FirebaseAuth.getInstance().currentUser?.email
            residentEmail?.let {
                val resident = UserOperations.getResident(it)
                _fullName.value = resident?.get("fullName").toString()
                _phoneNumber.value = resident?.get("phoneNumber").toString()
                _email.value = it
            }
        }
    }
}