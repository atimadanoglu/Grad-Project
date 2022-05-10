package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Notification
import kotlinx.coroutines.launch

class NotificationsResidentViewModel : ViewModel(){

    private val _notifications = MutableLiveData<ArrayList<Notification?>>()
    val notifications: LiveData<ArrayList<Notification?>> get() = _notifications

    private fun retrieveNotifications() = viewModelScope.launch {
            NotificationOperations.retrieveNotificationsForResident(_notifications)
    }
    init {
        retrieveNotifications()
    }

    fun clearNotifications() {
        viewModelScope.launch {
            NotificationOperations.deleteAllNotificationsForResident()
        }
    }
}