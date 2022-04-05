package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Notification
import kotlinx.coroutines.launch

class NotificationsResidentViewModel : ViewModel(){

    private val _notifications = MutableLiveData(arrayListOf<Notification>())
    val notifications: MutableLiveData<ArrayList<Notification>?> get() = _notifications

    fun retrieveNotifications(email: String) {
        viewModelScope.launch {
            val allNotifications = NotificationOperations.orderNotificationsByDateAndFetch(email)
            _notifications.value = allNotifications
        }
    }

    fun clearNotifications(email: String) {
        viewModelScope.launch {
            NotificationOperations.deleteAllNotificationsForResident(email)
        }
    }
}