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
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddAnnouncementViewModel: ViewModel() {

    private val _title = MutableLiveData("")
    val title: LiveData<String?> get() = _title
    private val _content = MutableLiveData("")
    val content: LiveData<String?> get() = _content
    private val _selectedPicture = MutableLiveData<Uri?>()
    val selectedPicture: LiveData<Uri?> get() = _selectedPicture
    private val _downloadUri = MutableLiveData<Uri?>()
    val downloadUri: LiveData<Uri?> get() = _downloadUri
    fun setTitle(value: String) { _title.value = value }
    fun setContent(value: String) { _content.value = value }
    fun setSelectedPicture(value: Uri?) { _selectedPicture.value = value }

    companion object {
        private const val TAG = "AddAnnouncementViewModel"
    }

    fun makeShareAnnouncementOperation(title: String, content: String) {
        viewModelScope.launch {

            uploadImage(_selectedPicture.value, _downloadUri)
            println("içi ${_downloadUri.value}")
            val notification = getNotificationInfo(title, content)
            AnnouncementOperations.saveAnnouncementIntoDB(notification)
            AnnouncementOperations.shareAnnouncementWithResidents(notification)

            val playerIDs = OneSignalOperations.takePlayerIDs()
            OneSignalOperations.postNotification(playerIDs, notification)
        }
    }
    private suspend fun uploadImage(selectedPicture: Uri?, uri: MutableLiveData<Uri?>) = withContext(Dispatchers.IO) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val storage = FirebaseStorage.getInstance()
        val imageReference = storage.reference.child("announcementDocuments").child(imageName)
        if (selectedPicture != null) {
            try {
                val uploadTask = imageReference.putFile(selectedPicture)
                val urlTask = uploadTask.continueWithTask { task->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uri.postValue(task.result)
                    } else {
                        Log.e(TAG, "uploadImage --> task is not successful!")
                    }
                }
                uri.postValue(urlTask.result)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            Log.e(TAG, "uploadImage --> selectedPicture is null")
        }
    }
    private fun getNotificationInfo(title: String, content: String): Notification {
        val uuid = UUID.randomUUID()
        return Notification(
            title,
            content,
            _downloadUri.value.toString(),
            uuid.toString(),
            Timestamp(Date())
        )
    }

}

