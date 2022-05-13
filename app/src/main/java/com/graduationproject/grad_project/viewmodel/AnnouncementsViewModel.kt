package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.AnnouncementOperations
import com.graduationproject.grad_project.model.Announcement

class AnnouncementsViewModel: ViewModel() {
    private val _announcements = MutableLiveData<ArrayList<Announcement?>>()
    val announcements: LiveData<ArrayList<Announcement?>> get() = _announcements

    private val _openMenuOptions = MutableLiveData<Boolean?>()
    val openMenuOptions: LiveData<Boolean?> get() = _openMenuOptions

    private val _announcement = MutableLiveData<Announcement?>()
    val announcement: LiveData<Announcement?> get() = _announcement

    fun deleteAnnouncement() {
        _announcement.value?.let {
            AnnouncementOperations.deleteAnnouncement(it)
        }
    }

    fun saveInfo(announcement: Announcement?) {
        announcement?.let {
            _announcement.value = announcement
        }
        _openMenuOptions.value = true
    }

    fun retrieveAnnouncements() {
        AnnouncementOperations.retrieveAnnouncements(_announcements)
    }
}