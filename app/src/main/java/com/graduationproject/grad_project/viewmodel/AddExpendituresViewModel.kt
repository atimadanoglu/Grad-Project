package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Expenditure
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddExpendituresViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "AddExpendituresViewModel"
    }

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val amount = MutableLiveData("")
    private var _downloadUri = ""
    private val _selectedImage = MutableLiveData<Uri?>()

    fun uploadImageAndShareExpenditure(selectedPicture: Uri?) = CoroutineScope(Dispatchers.IO).launch {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val storage = FirebaseStorage.getInstance()
        val imageReference = storage.reference.child("expendituresDocuments").child(imageName)
        if (selectedPicture != null) {
            try {
                val uploadTask = imageReference.putFile(selectedPicture)
                uploadTask.onSuccessTask {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        _downloadUri = uri.toString()
                        saveExpenditureAndSendPushNotification()
                        println(uri.toString())
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            Log.e(TAG, "uploadImage --> selectedPicture is null")
        }
    }

    private fun saveExpenditureAndSendPushNotification() = CoroutineScope(ioDispatcher).launch {
        val expenditure = createExpenditureModel()
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


    private fun sendPushNotification() = CoroutineScope(ioDispatcher).launch {
        val ids = OneSignalOperations.takePlayerIDs()
        val uuid = UUID.randomUUID()
        val notification = Notification(
            title.value.toString(),
            content.value.toString(),
            _downloadUri,
            uuid.toString(),
            Timestamp(Date())
        )
        OneSignalOperations.postNotification(ids, notification)
    }

    private fun createExpenditureModel(): Expenditure? {
        try {
            println("isNull -> ${isNull()}")
            if (!isNull()) {
                val uuid = UUID.randomUUID()
                return Expenditure(
                    uuid.toString(),
                    title.value.toString(),
                    content.value.toString(),
                    amount.value.toString().toLong(),
                    _downloadUri,
                    Timestamp(Date())
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "createExpenditureViewModel ---> $e")
            return null
        }
        return null
    }
    fun isNull() = title.value.isNullOrEmpty() || amount.value.isNullOrEmpty()
            || content.value.isNullOrEmpty()

}