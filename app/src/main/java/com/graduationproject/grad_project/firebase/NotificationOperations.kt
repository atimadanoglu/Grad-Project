package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.RemoteMessage
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.view.admin.AddAnnouncementFragment
import com.onesignal.OneSignal
import kotlinx.coroutines.tasks.await

object NotificationOperations: FirebaseConstants() {

    private const val TAG = "NotificationOperations"

    fun deleteNotificationInAPosition(adminRef: CollectionReference,
                           auth: FirebaseAuth,
                           notifications: ArrayList<Notification>,
                           position: Int
                           ) {
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
            }
    }
    suspend fun saveNotificationIntoResidentDB(emailsOfResidents: ArrayList<String>, notification: Notification) {
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