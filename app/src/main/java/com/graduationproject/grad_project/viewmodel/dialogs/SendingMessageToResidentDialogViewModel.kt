package com.graduationproject.grad_project.viewmodel.dialogs

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.model.Message
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.util.*
import kotlin.Exception

class SendingMessageToResidentDialogViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "AddingDebtDialogViewModel"
    }

    private var _title = ""
    val title get() = _title
    private var _content = ""
    val content get() = _content

    private var _isMessageSaved = false
    val isMessageSaved get() = _isMessageSaved

    fun setTitle(title: String) { _title = title }
    fun setContent(content: String) { _content = content }

    suspend fun saveMessageIntoDB(email: String, message: Message) {
        CoroutineScope(ioDispatcher).launch {
            try {
                println("title.value: $_title")
                println("content.vlaue. $_content")
                MessagesOperations.saveMessageIntoDB(email, message)
                _isMessageSaved = true
            } catch (e: Exception) {
                _isMessageSaved = false
                Log.e(TAG, "saveMessageIntoDB ---> $e")
            }
        }.join()
    }

    suspend fun takePlayerIdAndSendPostNotification(resident: SiteResident) {
        CoroutineScope(ioDispatcher).launch {
            val notification = Notification(
                _title,
                _content,
                "",
                "",
                Timestamp(Date())
            )
            val playerID = arrayListOf(resident.player_id)
            println("resident player_id --> ${resident.player_id}")
            try {
                OneSignalOperations.postNotification(playerID, notification)
                println("post successful")
            } catch (e: Exception) {
                Log.e(TAG, "takePlayerIdsAndSendPostNotification --> $e")
            }
        }.join()
    }

}