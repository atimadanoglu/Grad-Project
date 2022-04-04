package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.graduationproject.grad_project.model.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object NotificationOperations: FirebaseConstants() {

    private const val TAG = "NotificationOperations"

    fun deleteNotificationInAPosition(adminRef: CollectionReference,
                           auth: FirebaseAuth,
                           notifications: ArrayList<Notification>,
                           position: Int
                           ) {
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            try {
                adminRef.document(auth.currentUser?.email.toString())
                    .collection("announcements")
                    .document(notifications[position].id)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "Deleting announcement operation is SUCCESSFUL!")
                        } else {
                            Log.w(TAG, "Deleting announcement is UNSUCCESSFUL!")
                        }
                    }.await()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
    suspend fun saveNotificationIntoResidentDB(emailsOfResidents: ArrayList<String>, notification: Notification) {
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
                        }.await()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

}