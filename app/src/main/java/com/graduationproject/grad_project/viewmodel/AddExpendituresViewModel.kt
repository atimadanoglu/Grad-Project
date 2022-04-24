package com.graduationproject.grad_project.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
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
            _downloadUri.value = uri
            StorageOperations.uploadImage(_selectedImage.value)
        }
    }

    suspend fun saveExpenditureIntoDB(
        title: String,
        content: String,
        amount: Int,
        uri: Uri
    ) {
        CoroutineScope(ioDispatcher).launch {
            val email = async {
                FirebaseAuth.getInstance().currentUser?.email
            }
            val expenditure = async(Dispatchers.Main) {
                createExpenditureModel(title, content, amount, uri)
            }

            launch {
                email.await()?.let { email ->
                    expenditure.await()?.let { expenditure ->
                        UserOperations.saveExpenditure(email, expenditure)
                    }
                }
            }
            launch {
                email.await()?.let { email ->
                    expenditure.await()?.let { expenditure ->
                        UserOperations.updateExpenditureAmount(email, expenditure)
                    }
                }
            }
            launch {
                email.await()?.let { email ->
                    expenditure.await()?.let { expenditure ->
                        SiteOperations.saveExpenditure(email, expenditure)
                    }
                }
            }
            launch {
                sendPushNotification()
            }
        }
    }

    private suspend fun sendPushNotification() {
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

    private fun createExpenditureModel(
        title: String,
        content: String,
        amount: Int,
        uri: Uri
    ): Expenditure? {
        try {
            _title.value = title
            _content.value = content
            _amount.value = amount
            _downloadUri.value = uri
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