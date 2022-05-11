package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import android.util.Log
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
import java.util.*

class AddAnnouncementViewModel: ViewModel() {

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    private val _selectedPicture = MutableLiveData<Uri?>()
    fun setSelectedPicture(value: Uri?) { _selectedPicture.value = value }
    private var _downloadUri = ""

    private var notification: Notification? = Notification()

    companion object {
        private const val TAG = "AddAnnouncementViewModel"
    }

    private fun makeShareAnnouncementOperation(uri: String?) = viewModelScope.launch {
        println("içi $_downloadUri")
        notification = getNotificationInfo(uri)
        notification?.let {
            AnnouncementOperations.saveAnnouncementIntoDB(it)
            AnnouncementOperations.shareAnnouncementWithResidents(it)

            val playerIDs = OneSignalOperations.takePlayerIDs()
            OneSignalOperations.postNotification(playerIDs, it)
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
                        makeShareAnnouncementOperation(_downloadUri)
                        println(uri.toString())
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "uploadImageAndShareNotification --> $e")
            }
        } else {
            Log.e(TAG, "uploadImageAndShareNotification --> selectedPicture is null")
        }
    }
    private fun getNotificationInfo(uri: String?): Notification? {
        val uuid = UUID.randomUUID()
        if (!areNull()) {
            return Notification(
                title.value!!,
                content.value!!,
                uri,
                uuid.toString(),
                Timestamp(Date())
            )
        }
        return null
    }

    fun areNull() = title.value == null && content.value == null
            && _selectedPicture.value == null
}
