package com.graduationproject.grad_project.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel

class AdminNewAccountViewModel: ViewModel() {

    private var _fullName = ""
    val fullName get() = _fullName

    private var _phoneNumber = ""
    val phoneNumber get() = _phoneNumber

    private var _email = ""
    val email get() = _email

    private var _password = ""
    val password get() = _password

    private var _activationKey = ""
    val activationKey get() = _activationKey

    fun setFullName(fullName: String) { _fullName = fullName }
    fun setPhoneNumber(phoneNumber: String) { _phoneNumber = phoneNumber }
    fun setEmail(email: String) { _email = email }
    fun setPassword(password: String) { _password = password }
    fun setActivationKey(key: String) { _activationKey = key }

    fun isValidEmailAddress(): Boolean {
        if (email.isBlank() || email.isEmpty()) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}