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

class DeletingDebtDialogViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    companion object {
        const val TAG = "AddingDebtDialogViewModel"
    }
    private var _deletingCause = ""
    val deletingCause get() = _deletingCause

    private var _deletedAmount = 0L
    val deletedAmount get() = _deletedAmount

    private var _isDebtUpdated = false
    val isDebtUpdated get() = _isDebtUpdated

    fun setCause(deletingCause: String) { _deletingCause = deletingCause }
    fun setDeletedAmount(deletedAmount: Long) { _deletedAmount = deletedAmount }

    suspend fun deleteDebt(email: String, deletedAmount: Long) {
        CoroutineScope(ioDispatcher).launch {
            try {
                UserOperations.deleteDebt(email, deletedAmount)
                withContext(mainDispatcher) {
                    _isDebtUpdated = true
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "deleteDebt --> $e")
            }
        }.join()
    }

    suspend fun takePlayerIdAndSendPostNotification(resident: SiteResident) {
        CoroutineScope(mainDispatcher).launch {
            val message = "$_deletedAmount TL borcunuz silindi!"
            val uuid = UUID.randomUUID()
            val notification = Notification(
                _deletingCause,
                message,
                "",
                uuid.toString(),
                Timestamp.now()
            )
            val playerID = arrayListOf<String?>(resident.player_id)
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