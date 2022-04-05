package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    private var _email = ""
    val email get() = _email

    private var _password = ""
    val password get() = _password

}