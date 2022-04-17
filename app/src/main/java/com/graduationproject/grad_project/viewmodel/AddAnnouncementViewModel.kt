package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.AnnouncementOperations
import com.graduationproject.grad_project.firebase.StorageOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*

class AddAnnouncementViewModel: ViewModel() {

    private var _title: String = ""
    val title: String
        get() = _title
    private var _content: String = ""
    val content: String
        get() = _content
    private var _selectedPicture: Uri? = null
    val selectedPicture: Uri?
        get() = _selectedPicture


    companion object {
        private const val TAG = "AddAnnouncementViewModel"
    }

    fun setTitle(title: String) {
        _title = title
    }
    fun setContent(content: String) {
        _content = content
    }
    fun setSelectedPicture(selectedPicture: Uri?) {
        _selectedPicture = selectedPicture
    }

    private val coroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
    }

    suspend fun makeShareAnnouncementOperation(email: String, notification: Notification, view: View) {
        AnnouncementOperations.saveAnnouncementIntoDB(email, notification.id, notification)
        AnnouncementOperations.shareAnnouncementWithResidents(notification)

        StorageOperations.uploadImage(selectedPicture)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val playerIDs = OneSignalOperations.takePlayerIDs(email)
            OneSignalOperations.postNotification(playerIDs, notification)
        }
    }

}

