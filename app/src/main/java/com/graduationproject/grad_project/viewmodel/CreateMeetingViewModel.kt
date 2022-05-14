package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Meeting
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CreateMeetingViewModel: ViewModel() {
    private val _residents = MutableLiveData<MutableList<SiteResident?>>()
    val residents: LiveData<MutableList<SiteResident?>> get() = _residents

    private val residentsPlayerIDs = MutableLiveData<ArrayList<String?>>()
    private val residentsEmails = MutableLiveData<MutableList<String?>>()

    val meetingTitle = MutableLiveData("")
    private val _hour = MutableLiveData<Long?>()
    private val _minute = MutableLiveData<Long?>()

    private var notification: Notification? = null

    private val _navigateToMeetingsFragment = MutableLiveData<Boolean?>()
    val navigateToMeetingsFragment: LiveData<Boolean?> get() = _navigateToMeetingsFragment

    fun setHour(value: Long) { _hour.value = value }
    fun setMinute(value: Long) { _minute.value = value }

    fun setPlayerIdsAndEmails() = viewModelScope.launch {
        _residents.value?.forEach {
            val ids = arrayListOf<String?>()
            val emails = mutableListOf<String?>()
            ids.add(it?.player_id)
            emails.add(it?.email)
            residentsPlayerIDs.value = ids
            residentsEmails.value = emails
        }
    }

    fun retrieveResidents() {
        UserOperations.retrieveResidentsInSpecificSite(_residents)
    }

    private fun sendPushNotification() = viewModelScope.launch {
        val uuid = UUID.randomUUID()
        meetingTitle.value?.let {
            notification = Notification(
                it,
                "Bugün saat ${_hour.value}:${_minute.value} de toplantı yapılacaktır. Katılmanız önemle rica olunur!",
                "",
                uuid.toString(),
                Timestamp.now()
            )
            residentsPlayerIDs.value?.let { list ->
                OneSignalOperations.postNotification(
                    list,
                    notification!!
                )
            }
        }
    }

    private fun areTheyNull() = meetingTitle.value.isNullOrEmpty() || _hour.value == null
            || _minute.value == null

    private fun saveMeetingInfoDB() = viewModelScope.launch {
        val uuid = UUID.randomUUID()
        if (!areTheyNull()) {
            val meeting = Meeting(
                uuid.toString(),
                meetingTitle.value!!,
                _hour.value!!,
                _minute.value!!,
                "",
                Timestamp.now()
            )
            SiteOperations.saveMeetingInfo(meeting)
        }
    }

    fun createMeetingButtonClicked() {
        if (residentsPlayerIDs.value?.isNotEmpty() == true) {
            sendPushNotification()
        }
        saveMeetingInfoDB()
        if (residentsEmails.value?.isNotEmpty() == true) {
            saveMeetingIntoNotificationCollectionForResident()
        }
        _navigateToMeetingsFragment.value = true
    }

    fun saveMeetingIntoNotificationCollectionForResident() {
        notification?.let { UserOperations.saveMeetingNotification(residentsEmails, it) }
    }
}