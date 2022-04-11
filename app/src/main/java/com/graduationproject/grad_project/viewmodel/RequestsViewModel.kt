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
                    _requests.value = requests
            }
        }
    }

  /*  fun shareRequestWithAdmin(email: String, request: Request) {
        val residentEmail = FirebaseAuth.getInstance().currentUser?.email
        CoroutineScope(ioDispatcher).launch {
            launch {
                RequestsOperations.saveRequestIntoAdminDB(email, request)
            }
            launch {
                residentEmail?.let { residentEmail ->
                    RequestsOperations.saveRequestIntoResidentDB(residentEmail, request)
                }
            }
            launch {
                if (residentEmail != null) {
                    takePlayerIdAndPostNotification(residentEmail, request)
                }
            }
        }
    }

    suspend fun takePlayerIdAndPostNotification(
        residentEmail: String,
        request: Request
    ) {
        withContext(mainDispatcher) {
            try {
                val uuid = UUID.randomUUID()
                val notification = Notification(
                    request.title,
                    request.content,
                    "",
                    uuid.toString(),
                    Timestamp.now()
                )
                val resident = async(ioDispatcher) {
                    UserOperations.getResident(residentEmail)
                }
                val admin = async(ioDispatcher) {
                    resident.await()?.let { UserOperations.getAdminInSpecificSite(it) }
                }
                val list = arrayListOf<String>()
                admin.await()?.forEach {
                    list.add(it["player_id"].toString())
                }
                OneSignalOperations.postNotification(list, notification)
            } catch (e: Exception) {
                Log.e(TAG, "takePlayerIdAndPostNotification --> $e")
            }
        }
    }*/

}