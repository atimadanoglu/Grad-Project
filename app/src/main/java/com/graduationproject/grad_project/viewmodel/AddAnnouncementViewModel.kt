package com.graduationproject.grad_project.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.graduationproject.grad_project.FirebaseService
import com.graduationproject.grad_project.RetrofitInstance
import com.graduationproject.grad_project.firebase.AnnouncementOperations
import com.graduationproject.grad_project.firebase.StorageOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.PushNotification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import com.graduationproject.grad_project.view.admin.AddAnnouncementFragment
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

        StorageOperations.uploadImage(view, selectedPicture)

        viewModelScope.launch {
            val playerIDs = OneSignalOperations.takePlayerIDs(email)
            OneSignalOperations.postNotification(playerIDs, notification)
        }
    }

}

