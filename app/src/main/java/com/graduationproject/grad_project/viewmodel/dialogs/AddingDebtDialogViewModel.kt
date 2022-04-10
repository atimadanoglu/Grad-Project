package com.graduationproject.grad_project.viewmodel.dialogs

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.SiteResident
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.util.*

class AddingDebtDialogViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    companion object {
        const val TAG = "AddingDebtDialogViewModel"
    }
    private var _debtTitle = ""
    val debtTitle get() = _debtTitle

    private var _debtAmount = 0.0
    val debtAmount get() = _debtAmount

    private var _isDebtUpdated = false
    val isDebtUpdated get() = _isDebtUpdated

    fun setTitle(title: String) { _debtTitle = title }
    fun setAmount(amount: Double) { _debtAmount = amount }

    suspend fun addDebt(email: String, debtAmount: Double) {
        CoroutineScope(ioDispatcher).launch {
            try {
                UserOperations.addDebt(email, debtAmount)
                withContext(mainDispatcher) {
                    _isDebtUpdated = true
                }
            } catch (e: FirebaseFirestoreException) {

            }
        }.join()
    }

    suspend fun takePlayerIdAndSendPostNotification(resident: SiteResident) {
        CoroutineScope(mainDispatcher).launch {
            val message = "$_debtAmount TL borç eklendi!"
            val uuid = UUID.randomUUID()
            val notification = Notification(
                _debtTitle,
                message,
                "",
                uuid.toString(),
                Timestamp.now()
            )
            val playerID = arrayListOf(resident.player_id)
            try {
                withContext(ioDispatcher) {
                    OneSignalOperations.postNotification(playerID, notification)
                    NotificationOperations.saveNotificationForSpecificResident(resident.email, notification)
                }
            } catch (e: Exception) {
                Log.e(TAG, "takePlayerIdsAndSendPostNotification --> $e")
            }
        }.join()
    }
}

