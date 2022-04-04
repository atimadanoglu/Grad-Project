package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Notification

class NotificationsResidentViewModel : ViewModel(){

    private val _notifications = MutableLiveData(arrayListOf<Notification>())
    val notifications: MutableLiveData<ArrayList<Notification>>
        get() = _notifications

    suspend fun retrieveNotifications(email: String) {
        val allNotifications = NotificationOperations.orderNotificationsByDateAndFetch(email, arrayListOf())
        _notifications.value = allNotifications
    }

}