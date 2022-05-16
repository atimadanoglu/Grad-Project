package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HomeResidentViewModel: ViewModel() {

    private var _myDebt = MutableLiveData(0L)
    val myDebt: LiveData<Long?> get() = _myDebt
    private var _myRequestsAmount = MutableLiveData(0L)
    val myRequestsAmount: LiveData<Long?> get() = _myRequestsAmount
    private var _myNotificationsCount = MutableLiveData(0L)
    val myNotificationsCount: LiveData<Long?> get() = _myNotificationsCount

    fun retrieveMyDebtInfo() = UserOperations.retrieveResidentDebt(_myDebt)
    fun retrieveMyRequestsCount() = UserOperations.retrieveResidentRequestsCount(_myRequestsAmount)
    fun retrieveMyNotificationsCount() = UserOperations.retrieveResidentNotificationCount(_myNotificationsCount)

   /* fun retrieveResidentInformation(
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
                    val debt = resident.await()?.get("debt").toString()
                    if (debt.isNotEmpty() && debt != "null") {
                        _myDebt.postValue(resident.await()?.get("debt").toString().toLong())
                    }
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
    }*/

}