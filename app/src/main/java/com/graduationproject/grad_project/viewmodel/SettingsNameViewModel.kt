package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.launch

class SettingsNameViewModel: ViewModel() {

    private val _name = MutableLiveData("")
    val name: LiveData<String> get() = _name
    private val _isSame = MutableLiveData(false)
    val isSame: LiveData<Boolean> get() = _isSame

    fun setName(name: String) { _name.value = name }

    fun updateName(value: String) {
        viewModelScope.launch {
            setName(value)
            UserOperations.updateFullNameForAdmin(value)
        }
    }

    fun checkPreviousAndNewName() {
        viewModelScope.launch {
            val displayName = FirebaseAuth.getInstance().currentUser?.displayName
            displayName?.let {
                _isSame.value = displayName == _name.value
            }
        }
    }
}