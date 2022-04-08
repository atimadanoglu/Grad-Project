package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IncomingMessagesResidentViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _messages = MutableLiveData(arrayListOf<Message>())
    val messages: MutableLiveData<ArrayList<Message>?> get() = _messages

    fun retrieveMessages(email: String) {
        viewModelScope.launch(ioDispatcher) {
            val allMessages = MessagesOperations.retrieveMessages(email)
            _messages.value = allMessages
        }
    }

    fun clearMessages(email: String) {
        viewModelScope.launch(ioDispatcher) {
            MessagesOperations.deleteAllMessages(email)
            _messages.value = arrayListOf()
        }
    }
}