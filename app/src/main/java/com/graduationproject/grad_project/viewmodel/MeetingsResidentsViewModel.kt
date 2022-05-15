package com.graduationproject.grad_project.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.model.Meeting

class MeetingsResidentsViewModel: ViewModel() {

    private val _meetingInfo = MutableLiveData<Meeting?>()
    val meetingInfo: LiveData<Meeting?> get() = _meetingInfo

    private val _isValidUri = MutableLiveData<Boolean?>()
    val isValidUri: LiveData<Boolean?> get() = _isValidUri

    fun retrieveMeeting() {
        SiteOperations.retrieveMeetingForResident(_meetingInfo)
    }

    fun checkLinkValidity(text: String) {
        _isValidUri.value = Patterns.WEB_URL.matcher(text).matches()
    }
}