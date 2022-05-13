package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Notification

class NotificationsAdminViewModel: ViewModel() {

    private val _notifications = MutableLiveData<MutableList<Notification?>>()
    val notifications: LiveData<MutableList<Notification?>> get() = _notifications

    private val _notification = MutableLiveData<Notification?>()
    val notification: LiveData<Notification?> get() = _notification

    private val _openMenuOptions = MutableLiveData<Boolean?>()
    val openMenuOptions: LiveData<Boolean?> get() = _openMenuOptions

    fun retrieveNotifications() = NotificationOperations.retrieveNotificationsForAdmin(_notifications)

    fun saveInfo(notification: Notification) {
        _notification.value = notification
        _openMenuOptions.value = true
    }

    override fun onCleared() {
        super.onCleared()
        retrieveNotifications().cancel()
    }
}