package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.model.Request

class AdminRequestsListViewModel: ViewModel() {

    private val _requests = MutableLiveData<List<Request?>>()
    val requests: LiveData<List<Request?>> get() = _requests

    private val _request = MutableLiveData<Request?>()
    val request: LiveData<Request?> get() = _request

    private val _openMenuOptions = MutableLiveData<Boolean?>()
    val openMenuOptions: LiveData<Boolean?> get() = _openMenuOptions

    fun retrieveRequests() {
        RequestsOperations.retrieveRequestsForAdmin(_requests)
    }

    fun deleteRequest() {
        _request.value?.let { RequestsOperations.deleteRequestFromAdminDB(it) }
    }

    fun saveInfo(request: Request) {
        _request.value = request
        _openMenuOptions.value = true
    }
}