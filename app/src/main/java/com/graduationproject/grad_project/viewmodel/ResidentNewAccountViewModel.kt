package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.ViewModel

class ResidentNewAccountViewModel: ViewModel() {

    private var _fullName = ""
    val fullName get() = _fullName

    private var _phoneNumber = ""
    val phoneNumber get() = _phoneNumber

    private var _email = ""
    val email get() = _email

    private var _password = ""
    val password get() = _password

}