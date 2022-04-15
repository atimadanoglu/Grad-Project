package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Message
import kotlinx.coroutines.*

class IncomingMessagesResidentViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    private val _messages = MutableLiveData(arrayListOf<Message>())
    val messages: MutableLiveData<ArrayList<Message>?> get() = _messages

    fun retrieveMessages(email: String) {
        viewModelScope.launch(ioDispatcher) {
            val allMessages = async {
                MessagesOperations.retrieveMessages(email)
            }
            withContext(uiDispatcher) {
                _messages.value = allMessages.await()
            }
        }
    }

    fun clearMessages(email: String) {
        viewModelScope.launch(ioDispatcher) {
            MessagesOperations.deleteAllMessages(email)
            withContext(uiDispatcher) {
                _messages.value = arrayListOf()
            }
        }
    }
}