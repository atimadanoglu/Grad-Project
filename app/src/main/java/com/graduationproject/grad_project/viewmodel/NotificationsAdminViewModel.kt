package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Notification

class NotificationsAdminViewModel: ViewModel() {

    private val _notifications = MutableLiveData<MutableList<Notification?>>()
    val notifications: LiveData<MutableList<Notification?>> get() = _notifications

    fun retrieveNotifications() = NotificationOperations.retrieveNotificationsForAdmin(_notifications)

    override fun onCleared() {
        super.onCleared()
        retrieveNotifications().cancel()
    }
}