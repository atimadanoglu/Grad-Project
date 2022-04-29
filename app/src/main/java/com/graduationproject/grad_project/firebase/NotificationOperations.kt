package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.model.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object NotificationOperations: FirebaseConstants() {

    private const val TAG = "NotificationOperations"

    fun deleteNotificationInAPosition(notification: Notification) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                currentUserEmail?.let {
                    residentRef.document(it)
                        .collection("notifications")
                        .document(notification.id)
                        .delete()
                        .await().also {
                            Log.d(TAG, "Deleting announcement operation is SUCCESSFUL!")
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteNotificationInAPosition ---> $e")
            }
        }
    }

    fun deleteNotificationForAdmin(notification: Notification) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                adminRef.document(it)
                    .collection("notifications")
                    .document(notification.id)
                    .delete()
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "deleteNotificationForAdmin --> $e")
        }
    }

    fun saveNotificationIntoResidentDB(emailsOfResidents: ArrayList<String>, notification: Notification) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            for (emailOfResident in emailsOfResidents) {
                try {
                    residentRef.document(emailOfResident)
                        .collection("notifications")
                        .document(notification.id)
                        .set(notification)
                        .addOnSuccessListener {
                            Log.d(TAG, "Announcement write is SUCCESSFUL!")
                        }.addOnFailureListener { exception ->
                            Log.w(TAG, "Announcement write is UNSUCCESSFUL!", exception)
                        }
                    Log.d(TAG, "saveNotificationIntoResidentDB --> Notification SUCCESSFULLY saved!")

                } catch (e: Exception) {
                    Log.e(TAG, "saveNotificationIntoResidentDB --> $e")
                }
            }
        }
    }

    fun saveNotificationForSpecificResident(email: String, notification: Notification) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                residentRef.document(email)
                    .collection("notifications")
                    .document(notification.id)
                    .set(notification)
                    .await()
                Log.d(TAG, "saveNotificationForSpecificResident --> Notification SUCCESSFULLY saved!")
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveNotificationForSpecificResident --> $e")
            }
        }
    }

    fun deleteAllNotificationsForResident() {
       CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                currentUserEmail?.let { email ->
                    residentRef.document(email)
                        .collection("notifications")
                        .get()
                        .await()
                        .forEach {
                            it.reference.delete()
                        }
                    Log.d(TAG, "Deleting notifications operation is SUCCESSFUL!")
                }
            } catch (e: FirebaseException) {
                Log.e(TAG, "deleteAllNotificationsForResident --> $e")
            }
        }
    }


    fun retrieveNotificationsForResident(notifications: MutableLiveData<ArrayList<Notification?>>)
        = CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
        try {
            currentUserEmail?.let {
                residentRef.document(it)
                    .collection("notifications")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveNotifications --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedNotifications = arrayListOf<Notification?>()
                        documents?.forEach { document ->
                            retrievedNotifications.add(document.toObject<Notification>())
                        }.also {
                            notifications.postValue(retrievedNotifications)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveNotifications --> $e")
        }
    }

    fun retrieveNotificationsForAdmin(notifications: MutableLiveData<MutableList<Notification?>>)
        = CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
        try {
            currentUserEmail?.let {
                adminRef.document(it)
                    .collection("notifications")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveNotifications --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedNotifications = mutableListOf<Notification?>()
                        documents?.forEach { document ->
                            retrievedNotifications.add(document.toObject<Notification>())
                        }.also {
                            notifications.postValue(retrievedNotifications)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveNotifications --> $e")
        }
    }

}