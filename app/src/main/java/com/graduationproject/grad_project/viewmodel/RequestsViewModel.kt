package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.model.Request

class RequestsViewModel: ViewModel() {
    private val _requests = MutableLiveData<ArrayList<Request?>>()
    val requests: LiveData<ArrayList<Request?>> get() = _requests

    fun retrieveRequests() {
        RequestsOperations.retrieveRequestsForResident(_requests)
    }
}