package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.launch

class SettingsAdminViewModel: ViewModel() {

    private val _fullName = MutableLiveData("")
    val fullName: LiveData<String> get() = _fullName
    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String> get() = _phoneNumber
    private val _email = MutableLiveData("")
    val email: LiveData<String> get() = _email
    private val _password = MutableLiveData("*********")
    val password: LiveData<String> get() = _password

    fun getAdmin() {
        viewModelScope.launch {
            val adminEmail = FirebaseAuth.getInstance().currentUser?.email
            adminEmail?.let {
                val admin = UserOperations.getAdmin(it)
                _fullName.value = admin?.get("fullName").toString()
                _phoneNumber.value = admin?.get("phoneNumber").toString()
                _email.value = it
            }
        }
    }

}