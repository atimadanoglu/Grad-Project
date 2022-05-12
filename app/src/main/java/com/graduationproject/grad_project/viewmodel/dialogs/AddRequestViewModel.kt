package com.graduationproject.grad_project.viewmodel.dialogs

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Administrator
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddRequestViewModel: ViewModel() {

    companion object {
        const val TAG = "AddRequestViewModel"
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val type = MutableLiveData("")

    private val _selectedPicture = MutableLiveData<Uri?>()
    fun setSelectedPicture(value: Uri?) { _selectedPicture.value = value }
    private var _downloadUri = ""

    private val _admin = MutableLiveData<Administrator?>()
    val admin: LiveData<Administrator?> get() = _admin

    private val _resident = MutableLiveData<SiteResident?>()
    val resident: LiveData<SiteResident?> get() = _resident

    private val _playerId = MutableLiveData<ArrayList<String?>>()
    val playerId: LiveData<ArrayList<String?>> get() = _playerId

    private val _notification = MutableLiveData<Notification?>()
    val notification: LiveData<Notification?> get() = _notification

    private val _request = MutableLiveData<Request?>()
    val request: LiveData<Request?> get() = _request

    private val _isImageUriNull = MutableLiveData<Boolean?>()
    val isImageUriNull: LiveData<Boolean?> get() = _isImageUriNull

    fun isNull() = title.value.isNullOrEmpty()
            || content.value.isNullOrEmpty() || type.value.isNullOrEmpty()

    fun getResidentInfo() {
        auth.currentUser?.email?.let {
            UserOperations.takeResident(it, _resident)
        }
    }

    fun getAdminInfo() = viewModelScope.launch {
        _resident.value?.let {
            UserOperations.takeAdminInSpecificSite(it, _admin)
        }
    }

    fun setPlayerID(playerID: String?) {
        val ids = arrayListOf<String?>()
        ids.add(playerID)
        _playerId.postValue(ids)
    }

    fun createNotification() {
        val uuid = UUID.randomUUID()
        _notification.postValue(Notification(
            title.value!!,
            content.value!!,
            _downloadUri,
            uuid.toString(),
            Timestamp.now()
        ))
    }

    fun createRequest() {
        val uuid = UUID.randomUUID()
        val sentBy = "${_resident.value?.fullName} " +
                "- Blok: ${resident.value?.blockNo} " +
                "- Daire: ${resident.value?.flatNo}"
        if (!isNull()) {
            val request = Request(
                uuid.toString(),
                title.value!!,
                content.value!!,
                type.value!!,
                _downloadUri,
                sentBy,
                Timestamp(Date())
            )
            _request.postValue(request)
        }
    }

    private fun saveRequestIntoAdminCollection() {
        val email = _admin.value?.email
        _request.value?.let {
            if (email != null) {
                RequestsOperations
                    .saveRequestIntoAdminDB(email, it)
            }
        }
    }

    private fun saveRequestIntoResidentCollection() {
        val email = _resident.value?.email
        _request.value?.let {
            if (email != null) {
                RequestsOperations
                    .saveRequestIntoResidentDB(email, it)
            }
        }
    }

    private fun saveRequestIntoNotificationCollection() {
        _notification.value?.let {
            RequestsOperations
                .saveRequestIntoNotificationCollection(it)
        }
    }

    fun sendPushNotification() {
        _playerId.value?.let { id ->
            notification.value?.let { notif ->
                OneSignalOperations.postNotification(id, notif)
            }
        }
    }

    fun saveTheseDataIntoDB() {
        saveRequestIntoAdminCollection()
        saveRequestIntoNotificationCollection()
        saveRequestIntoResidentCollection()
    }

    fun uploadImageAndShareNotification(selectedPicture: Uri?)
            = viewModelScope.launch(Dispatchers.IO) {
        if (selectedPicture != null) {
            try {
                val uuid = UUID.randomUUID()
                val imageName = "$uuid.jpeg"
                val storage = FirebaseStorage.getInstance()
                val imageReference = storage.reference.child("announcementDocuments").child(imageName)
                val uploadTask = imageReference.putFile(selectedPicture)
                uploadTask.onSuccessTask {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        _downloadUri = uri.toString()
                        _isImageUriNull.postValue(false)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "uploadImageAndShareNotification --> $e")
            }
        } else {
            createRequest()
            _isImageUriNull.postValue(true)
            saveTheseDataIntoDB()
            Log.e(TAG, "uploadImageAndShareNotification --> selectedPicture is null")
        }
    }
}