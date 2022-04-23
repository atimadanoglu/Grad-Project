package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.model.Message
import kotlinx.coroutines.launch

class IncomingMessagesResidentViewModel : ViewModel() {

    private val _messages = MutableLiveData<ArrayList<Message?>>()
    val messages: MutableLiveData<ArrayList<Message?>> get() = _messages

    fun retrieveMessages() {
        viewModelScope.launch {
            MessagesOperations.retrieveMessagesWithSnapshot(_messages)
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            MessagesOperations.deleteAllMessages()
        }
    }
}