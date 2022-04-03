package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.RemoteMessage
import com.graduationproject.grad_project.model.Notification
import com.onesignal.OneSignal

object NotificationOperations {

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

}