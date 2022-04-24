package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.StorageOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Expenditure
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.util.*

class AddExpendituresViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "AddExpendituresViewModel"
    }

    private val _title = MutableLiveData("")
    val title: LiveData<String> get() = _title
    private val _content = MutableLiveData("")
    val content: LiveData<String> get() = _content
    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> get() = _amount
    private val _downloadUri = MutableLiveData<Uri?>()
    val downloadUri: LiveData<Uri?> get() = _downloadUri
    private val _selectedImage = MutableLiveData<Uri?>()
    val selectedImage: LiveData<Uri?> get() = _selectedImage

    fun uploadDocument(uri: Uri) {
        viewModelScope.launch {
            _selectedImage.value = uri
            StorageOperations.uploadImage(uri)
        }
    }

    private suspend fun uploadImage(selectedPicture: Uri?) = withContext(Dispatchers.IO) {
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

                    } else {
                        Log.e(TAG, "uploadImage --> task is not successful!")
                    }
                }
                return@withContext urlTask.result
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            Log.e(TAG, "uploadImage --> selectedPicture is null")
        }
    }

    suspend fun uploadImage(
        title: String,
        content: String,
        amount: Int,
        selectedPicture: Uri?
    ) = withContext(Dispatchers.IO) {
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
                        _downloadUri.postValue(task.result)
                        val expenditure = createExpenditureModel(title, content, amount, selectedPicture)

                        if (expenditure != null) {
                            UserOperations.saveExpenditure(expenditure)
                        }

                        if (expenditure != null) {
                            UserOperations.updateExpenditureAmount(expenditure)
                        }
                        if (expenditure != null) {
                            SiteOperations.saveExpenditure(expenditure)
                        }

                        sendPushNotification()
                    } else {
                        Log.e(TAG, "uploadImage --> task is not successful!")
                    }
                }
                return@withContext urlTask.result
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            Log.e(TAG, "uploadImage --> selectedPicture is null")
        }
    }

    suspend fun saveExpenditureIntoDB(
        title: String,
        content: String,
        amount: Int,
        uri: Uri?
    ) {
        CoroutineScope(ioDispatcher).launch {

            val expenditure = withContext(Dispatchers.Main) {
                createExpenditureModel(title, content, amount, uri)
            }

            if (expenditure != null) {
                UserOperations.saveExpenditure(expenditure)
            }

            if (expenditure != null) {
                UserOperations.updateExpenditureAmount(expenditure)
            }
            if (expenditure != null) {
                SiteOperations.saveExpenditure(expenditure)
            }

            sendPushNotification()
        }
    }

    private fun sendPushNotification() {
        CoroutineScope(ioDispatcher).launch {
            val ids = OneSignalOperations.takePlayerIDs()
            val uuid = UUID.randomUUID()
            val notification = Notification(
                _title.value.toString(),
                _content.value.toString(),
                _downloadUri.value.toString(),
                uuid.toString(),
                Timestamp(Date())
            )
            OneSignalOperations.postNotification(ids, notification)
        }
    }

    private fun createExpenditureModel(
        title: String,
        content: String,
        amount: Int,
        uri: Uri?
    ): Expenditure? {
        try {
            _title.value = title
            _content.value = content
            _amount.value = amount
            _selectedImage.value = uri
            println("isNull -> ${isNull()}")
            if (!isNull()) {
                val uuid = UUID.randomUUID()
                return Expenditure(
                    uuid.toString(),
                    _title.value.toString(),
                    _content.value.toString(),
                    _amount.value?.toInt()!!,
                    _downloadUri.value.toString(),
                    Timestamp(Date())
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "createExpenditureViewModel ---> $e")
            return null
        }
        return null
    }
    private fun isNull() = _title.value.isNullOrEmpty() || _amount.value?.toInt() == 0
            || _content.value.isNullOrEmpty()

}


/*
class AddExpendituresViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "AddExpendituresViewModel"
    }

    private val id = UUID.randomUUID().toString()
    private val _title = MutableLiveData("")
    val title: LiveData<String> get() = _title
    private val _content = MutableLiveData("")
    val content: LiveData<String> get() = _content
    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> get() = _amount
    private val _downloadUri = MutableLiveData<Uri?>()
    val downloadUri: LiveData<Uri?> get() = _downloadUri
    private val _selectedImage = MutableLiveData<Uri?>()
    val selectedImage: LiveData<Uri?> get() = _selectedImage
    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val imageReference: StorageReference by lazy {
        storage.reference
    }
    var uri: Uri? = null
    fun setSelectedImage(value: Uri?) { _selectedImage.postValue(value) }

    fun uploadImage(selectedPicture: Uri?) {
        CoroutineScope(ioDispatcher).launch {
            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpeg"
            val imageReference = storage.reference.child("announcementDocuments").child(imageName)
            if (selectedPicture != null) {
                try {
                    val uploadTask = imageReference.putFile(selectedPicture)
                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageReference.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful && task.isComplete) {
                            task.result
                        } else {
                            Log.e(TAG, "uploadImage --> task is not successful!")
                        }
                    }
                    _downloadUri.postValue(urlTask.result)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            } else {
                Log.e(TAG, "uploadImage --> selectedPicture is null")
            }
        }
    }

    fun saveExpenditureIntoDB(expenditure: Expenditure) {
        SiteOperations.saveExpenditure(expenditure)
        UserOperations.saveExpenditure(expenditure)
        UserOperations.updateExpenditureAmount(expenditure)
    }
    fun sendPushNotification() {
        CoroutineScope(ioDispatcher).launch {
            val ids = OneSignalOperations.takePlayerIDs()
            val uuid = UUID.randomUUID()
            val notification = Notification(
                _title.value.toString(),
                _content.value.toString(),
                _downloadUri.value.toString(),
                uuid.toString(),
                Timestamp(Date())
            )
            OneSignalOperations.postNotification(ids, notification)
        }
    }

    fun createExpenditure(
        title: String,
        content: String,
        amount: Int
    ): Expenditure {
        val uuid = UUID.randomUUID()
        return Expenditure(
            uuid.toString(),
            title,
            content,
            amount,
            _downloadUri.value.toString(),
            Timestamp(Date())
        )
    }




    private fun isNull() = _title.value.isNullOrEmpty() || _amount.value?.toInt() == 0
            || _content.value.isNullOrEmpty()

}
*/
