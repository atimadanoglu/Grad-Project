package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HomeResidentViewModel: ViewModel() {

    private var _myDebt = MutableLiveData(0.0)
    val myDebt get() = _myDebt
    private var _myRequestsAmount = MutableLiveData(0)
    val myRequestsAmount get() = _myRequestsAmount
    private var _myNotificationsCount = MutableLiveData(0)
    val myNotificationsCount get() = _myNotificationsCount

    fun retrieveResidentInformation(
        auth: FirebaseAuth = FirebaseAuth.getInstance(),
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        CoroutineScope(ioDispatcher).launch {
            val email = auth.currentUser?.email
            email?.let {
                val resident = async {
                    UserOperations.getResident(it)
                }
                launch {
                    _myDebt.postValue(resident.await()?.get("debt").toString().toDouble())
                }

                launch {
                    val requests = resident.await()?.reference?.collection("requests")
                        ?.get()
                    withContext(mainDispatcher) {
                        var count = 0
                        requests?.await()?.forEach { _ ->
                            count++
                        }
                        _myRequestsAmount.postValue(count)
                    }
                }
                launch {
                    val notifications = resident.await()?.reference?.collection("notifications")
                        ?.get()
                    withContext(mainDispatcher) {
                        var count = 0
                        notifications?.await()?.forEach { _ ->
                            count++
                        }
                        _myNotificationsCount.postValue(count)
                    }
                }
            }
        }
    }

}