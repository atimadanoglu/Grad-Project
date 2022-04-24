package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.AnnouncementOperations
import com.graduationproject.grad_project.model.Announcement

class AnnouncementsViewModel: ViewModel() {
    private val _announcement = MutableLiveData<ArrayList<Announcement?>>()
    val announcement: LiveData<ArrayList<Announcement?>> get() = _announcement

    fun retrieveAnnouncements() {
        AnnouncementOperations.retrieveAnnouncements(_announcement)
    }
}