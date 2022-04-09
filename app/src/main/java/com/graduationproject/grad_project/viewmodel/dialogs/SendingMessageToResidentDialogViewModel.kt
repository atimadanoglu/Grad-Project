package com.graduationproject.grad_project.viewmodel.dialogs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.model.Message
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

class SendingMessageToResidentDialogViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "AddingDebtDialogViewModel"
    }

    private val _title = MutableLiveData<String?>()
    val title get() = _title
    private val _content = MutableLiveData<String?>()
    val content get() = _content

    private val _isMessageSaved = MutableLiveData<Boolean?>()
    val isMessageSaved get() = _isMessageSaved

    fun setTitle(title: String) {
        _title.value = title
    }
    fun setContent(content: String) {
        _content.value = content
    }

    suspend fun saveMessageIntoDB(email: String, message: Message) {
        viewModelScope.launch(ioDispatcher) {
            try {
                MessagesOperations.saveMessageIntoDB(email, message)
                _isMessageSaved.value = true
            } catch (e: Exception) {
                Log.e(TAG, "saveMessageIntoDB ---> $e")
                _isMessageSaved.value = false
            }
        }
    }

    suspend fun takePlayerIdAndSendPostNotification(
        resident: SiteResident,
        title: String,
        content: String
    ) {
        viewModelScope.launch {
            val playerId = arrayListOf(resident.playerID)
            val notification = Notification(
                title,
                content,
                "",
                "",
                Timestamp(Date())
            )
            withContext(ioDispatcher) {
                OneSignalOperations.postNotification(playerId, notification)
            }
        }
    }

}