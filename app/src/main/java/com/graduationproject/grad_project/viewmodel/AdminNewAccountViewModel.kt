package com.graduationproject.grad_project.viewmodel

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class AdminNewAccountViewModel: ViewModel() {

    val fullName = MutableLiveData("")
    val phoneNumber = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val activationCode = MutableLiveData("")

    companion object {
        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+.,*=]).{4,}$"
        const val ACTIVATION_KEY = "qwerty"
    }

    fun isActivationKeyCorrect() = activationCode.value == ACTIVATION_KEY

    fun isEmpty() = email.value.isNullOrBlank() || fullName.value.isNullOrBlank()
            || phoneNumber.value.isNullOrBlank() || password.value.isNullOrBlank()

    fun isValidEmail() = !TextUtils.isEmpty(email.value)
            && Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches()

    fun isValidPhoneNumber() = !TextUtils.isEmpty(phoneNumber.value)
            && Patterns.PHONE.matcher(phoneNumber.value.toString()).matches()

    fun isValidPassword(): Boolean {
        password.value?.let {
            val pattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
            val matcher: Matcher = pattern.matcher(password.value.toString())
            return matcher.matches()
        }
        return false
    }

    fun allAreValid() = isValidEmail() && isValidPassword() && isValidPhoneNumber()
}