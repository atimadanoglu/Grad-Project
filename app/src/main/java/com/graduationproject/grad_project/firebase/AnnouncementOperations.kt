package com.graduationproject.grad_project.firebase

import android.util.Log
import com.graduationproject.grad_project.firebase.NotificationOperations.saveNotificationIntoResidentDB
import com.graduationproject.grad_project.firebase.UserOperations.getAdmin
import com.graduationproject.grad_project.firebase.UserOperations.getResidentsInASpecificSite
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.view.admin.AddAnnouncementFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AnnouncementOperations: FirebaseConstants() {

    private const val TAG = "AnnouncementOperations"

    suspend fun saveAnnouncementIntoDB(adminEmail: String, notificationID: String, notification: Notification) {
        try {
            CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
                adminRef.document(adminEmail)
                    .collection("announcements")
                    .document(notificationID)
                    .set(notification)
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    suspend fun shareAnnouncementWithResidents(notification: Notification) {
        try {
            CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
                val email = auth.currentUser?.email
                email?.let {
                    val adminInfo = getAdmin(email)
                    adminInfo?.let {
                        val residents = getResidentsInASpecificSite(adminInfo)
                        Log.d(TAG, "get() successfully worked")
                        val emails = arrayListOf<String>()

                        if (residents != null) {
                            for (resident in residents) {
                                emails.add(resident["email"] as String)
                            }
                        }
                        launch {
                            saveNotificationIntoResidentDB(emails, notification)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}