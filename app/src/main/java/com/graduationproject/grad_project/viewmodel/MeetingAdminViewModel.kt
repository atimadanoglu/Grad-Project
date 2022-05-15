package com.graduationproject.grad_project.viewmodel

import android.util.Patterns
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

class MeetingAdminViewModel: ViewModel() {

    private val _residents = MutableLiveData<MutableList<SiteResident?>>()
    val residents: LiveData<MutableList<SiteResident?>> get() = _residents

    private val _residentsPlayerIDs = MutableLiveData<ArrayList<String?>>()
    private val residentsEmails = MutableLiveData<MutableList<String?>>()

    private val _meetings = MutableLiveData<List<Meeting?>>()
    val meetings: LiveData<List<Meeting?>> get() = _meetings

    private val _meeting = MutableLiveData<Meeting?>()
    val meeting: LiveData<Meeting?> get() = _meeting

    val meetingUri = MutableLiveData("")

    private val _isValidUri = MutableLiveData<Boolean?>()
    val isValidUri: LiveData<Boolean?> get() = _isValidUri

    private val _meetingID = MutableLiveData("")

    fun setMeetingID(id: String) { _meetingID.value = id }
    fun setMeeting(meeting: Meeting) { _meeting.value = meeting }

    fun checkLinkValidity(text: String) {
        _isValidUri.value = Patterns.WEB_URL.matcher(text).matches()
    }

    private var notification: Notification? = null

    private val _isLinkShared = MutableLiveData<Boolean?>()
    val isLinkShared: LiveData<Boolean?> get() = _isLinkShared

    fun setPlayerIdsAndEmails() = viewModelScope.launch {
        _residents.value?.forEach {
            val ids = arrayListOf<String?>()
            val emails = mutableListOf<String?>()
            ids.add(it?.player_id)
            emails.add(it?.email)
            _residentsPlayerIDs.value = ids
            residentsEmails.value = emails
        }
    }

    fun retrieveResidents() {
        UserOperations.retrieveResidentsInSpecificSite(_residents)
    }

    fun saveViewHolderData(meeting: Meeting) {
        _meeting.value = meeting
        _meetingID.value = meeting.id
    }

    fun retrieveMeetings() = viewModelScope.launch {
        SiteOperations.retrieveMeetings(_meetings)
    }

    private fun convertUriIntoValidFormatIfItIsNot() {
        if (meetingUri.value?.startsWith("https://") == false) {
            val newText = "https://" + meetingUri.value
            meetingUri.value = newText
        }
    }

    fun shareLinkButtonClicked() {
        if (_meetingID.value != null && meetingUri.value != null) {
            val message = "Toplantı başlamak üzere. Katılmanız önemle rica olunur!"
            convertUriIntoValidFormatIfItIsNot()
            SiteOperations.addMeetingUriIntoDB(
                _meetingID.value!!,
                meetingUri.value!!
            )
            sendPushNotificationForURI(message).also {
                meetingUri.value = ""
                _isLinkShared.value = true
            }
        }
    }

    private fun sendPushNotificationForURI(message: String) {
        val uuid = UUID.randomUUID()
        notification = Notification(
            "Toplantı",
            message,
            "",
            uuid.toString(),
            Timestamp.now()
        )
        _residentsPlayerIDs.value?.let {
            OneSignalOperations.postNotification(
                it,
                notification!!
            )
        }
    }

}