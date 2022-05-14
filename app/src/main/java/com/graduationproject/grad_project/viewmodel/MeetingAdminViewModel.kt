package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MeetingAdminViewModel: ViewModel() {

    private val _residents = MutableLiveData<ArrayList<String?>>()
    val residents: LiveData<ArrayList<String?>> get() = _residents

    val meetingTitle = MutableLiveData("")
    private val _hour = MutableLiveData<Long?>()
    private val _minute = MutableLiveData<Long?>()

    private var notification: Notification? = null

    private val _navigateToMeetingsFragment = MutableLiveData<Boolean?>()
    val navigateToMeetingsFragment: LiveData<Boolean?> get() = _navigateToMeetingsFragment

    fun setHour(value: Long) { _hour.value = value }
    fun setMinute(value: Long) { _minute.value = value }

    fun retrieveResidentsPlayerIDs() = viewModelScope.launch {
        UserOperations.retrieveResidentsPlayerIDs(_residents)
    }

    private fun sendPushNotification() {
        val uuid = UUID.randomUUID()
        notification = Notification(
            "Toplantı",
            "Bugün saat ${_hour.value}:${_minute.value} de toplantı yapılacaktır. Katılmanız önemle rica olunur!",
            "",
            uuid.toString(),
            Timestamp.now()
        )
        _residents.value?.let {
            OneSignalOperations.postNotification(
                it,
                notification!!
            )
        }
    }

    fun createMeetingButtonClicked() {
        if (_residents.value?.isNotEmpty() == true) {
            sendPushNotification()
        }
        _navigateToMeetingsFragment.value = true
    }

}