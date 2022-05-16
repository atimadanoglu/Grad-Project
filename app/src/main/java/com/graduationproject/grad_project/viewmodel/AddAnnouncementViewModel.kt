package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.grad_project.firebase.AnnouncementOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AddAnnouncementViewModel: ViewModel() {

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    private val _selectedPicture = MutableLiveData<Uri?>()
    val selectedPicture: LiveData<Uri?> get() = _selectedPicture
    fun setSelectedPicture(value: Uri?) { _selectedPicture.value = value }
    private var _downloadUri = ""

    private val _notification = MutableLiveData<Notification?>()
    val notification: LiveData<Notification?> get() = _notification

    private val _isShared = MutableLiveData<Boolean?>()
    val isShared: LiveData<Boolean?> get() = _isShared

    private val _isShareAnnouncementButtonClicked = MutableLiveData<Boolean?>()
    val isShareAnnouncementButtonClicked: LiveData<Boolean?> get() = _isShareAnnouncementButtonClicked

    private val _playerIDs = MutableLiveData<ArrayList<String?>>()
    val playerIDs: LiveData<ArrayList<String?>> get() = _playerIDs

    companion object {
        private const val TAG = "AddAnnouncementViewModel"
    }

    fun shareAnnouncementClicked() {
        _isShareAnnouncementButtonClicked.value = true
    }

    private fun takePlayerIDsAndSetNotification(downloadUri: String) {
        UserOperations.retrieveResidentsPlayerIDs(_playerIDs)
        val uuid = UUID.randomUUID()
        if (!areNull()) {
            _notification.value = Notification(
                title.value!!,
                content.value!!,
                downloadUri,
                uuid.toString(),
                Timestamp(Date())
            )
        }
    }

    fun saveAnnouncementIntoDB() = CoroutineScope(Dispatchers.IO).launch {
        notification.value?.let {
            AnnouncementOperations.saveAnnouncementIntoDB(it)
            AnnouncementOperations.shareAnnouncementWithResidents(it)
        }
    }

    fun sendPushNotification(playerIds: ArrayList<String?>) = CoroutineScope(Dispatchers.IO).launch {
        notification.value?.let {
            OneSignalOperations.postNotification(playerIds, it)
            _isShared.postValue(true)
        }
    }

    fun uploadImageAndShareNotification(selectedPicture: Uri?)
            = viewModelScope.launch(Dispatchers.IO) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val storage = FirebaseStorage.getInstance()
        val imageReference = storage.reference.child("announcementDocuments").child(imageName)
        if (selectedPicture != null) {
            try {
                val uploadTask = imageReference.putFile(selectedPicture)
                uploadTask.onSuccessTask {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        _downloadUri = uri.toString()
                        takePlayerIDsAndSetNotification(_downloadUri)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "uploadImageAndShareNotification --> $e")
            }
        } else {
            launch(Dispatchers.Main) {
                takePlayerIDsAndSetNotification("")
            }
            Log.e(TAG, "uploadImageAndShareNotification --> selectedPicture is null")
        }
    }

    private fun areNull() = title.value.isNullOrEmpty() || content.value.isNullOrEmpty()
}