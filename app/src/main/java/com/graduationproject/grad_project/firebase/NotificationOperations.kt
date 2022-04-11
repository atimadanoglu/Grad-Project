package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.graduationproject.grad_project.model.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

object NotificationOperations: FirebaseConstants() {

    private const val TAG = "NotificationOperations"

    fun deleteNotificationInAPosition(
                           notifications: ArrayList<Notification>,
                           position: Int) {
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            try {
                adminRef.document(auth.currentUser?.email.toString())
                    .collection("notifications")
                    .document(notifications[position].id)
                    .delete()
                    .await()
                Log.d(TAG, "Deleting announcement operation is SUCCESSFUL!")
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
    fun saveNotificationIntoResidentDB(emailsOfResidents: ArrayList<String>, notification: Notification) {
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
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

    fun deleteAllNotificationsForResident(email: String) {
       CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            try {
                residentRef.document(email)
                    .collection("notifications")
                    .get()
                    .addOnSuccessListener {
                        it.forEach {  document ->
                            document.reference.delete()
                        }
                    }
                Log.d(TAG, "Deleting notifications operation is SUCCESSFUL!")
            } catch (e: FirebaseException) {
                Log.e(TAG, e.toString())
            }
        }
    }


    suspend fun orderNotificationsByDateAndFetch(email: String): ArrayList<Notification> {
        return withContext(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val notifications = arrayListOf<Notification>()
                residentRef.document(email)
                    .collection("notifications")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await().forEach { document->
                        notifications.add(
                            Notification(
                                document.get("title") as String,
                                document.get("message") as String,
                                document.get("pictureUri") as String,
                                document.get("id") as String,
                                Timestamp(Date())
                            )
                        )
                    }
                notifications
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                arrayListOf()
            }
        }
    }

}