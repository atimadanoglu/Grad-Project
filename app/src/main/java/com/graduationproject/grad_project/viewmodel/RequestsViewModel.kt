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
    private val _requests = MutableLiveData<ArrayList<Request?>>()
    val requests: MutableLiveData<ArrayList<Request?>> get() = _requests

    suspend fun retrieveRequests(auth: FirebaseAuth = FirebaseAuth.getInstance()) {
        withContext(ioDispatcher) {
            val email = async {
                auth.currentUser?.email.toString()
            }
            val requests = async {
                RequestsOperations.getRequestsOfResidentsThemselves(email.await())
            }
            withContext(mainDispatcher) {
                if (requests.await().isNotEmpty() && !requests.await().contains(null))
                    _requests.postValue(requests.await())
            }
        }
    }
}