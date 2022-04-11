package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class RequestsViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "RequestsViewModel"
    }
    private val _requests = MutableLiveData<ArrayList<Request?>>(arrayListOf())
    val requests: MutableLiveData<ArrayList<Request?>> get() = _requests

    fun retrieveRequests(email: String) {
        viewModelScope.launch(ioDispatcher) {
            val requests = RequestsOperations.getRequestsOfResidentsThemselves(email)
            withContext(mainDispatcher) {
                if (requests.isNotEmpty() && !requests.contains(null))
                    _requests.postValue(requests)
            }
        }
    }

}