package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.firebase.NotificationOperations.saveNotificationIntoResidentDB
import com.graduationproject.grad_project.firebase.UserOperations.getAdmin
import com.graduationproject.grad_project.firebase.UserOperations.getResidentsInASpecificSite
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AnnouncementOperations: FirebaseConstants() {

    private const val TAG = "AnnouncementOperations"

    fun saveAnnouncementIntoDB(notification: Notification) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                currentUserEmail?.let {
                    adminRef.document(it)
                        .collection("announcements")
                        .document(notification.id)
                        .set(notification)
                        .await()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    fun shareAnnouncementWithResidents(notification: Notification) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
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
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

    }

    fun retrieveAnnouncements(announcements: MutableLiveData<ArrayList<Announcement?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            currentUserEmail?.let {
                adminRef.document(it)
                    .collection("announcements")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveAnnouncements -> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedAnnouncements = arrayListOf<Announcement?>()
                        documents?.forEach { document ->
                            retrievedAnnouncements.add(
                                document.toObject<Announcement>()
                            )
                        }.also {
                            announcements.value = retrievedAnnouncements
                        }
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "retrieveAnnouncements --> $e")
        }
    }

    fun deleteAnnouncement(announcement: Announcement) = CoroutineScope(ioDispatcher).launch {
        try {
            currentUserEmail?.let {
                adminRef.document(it)
                    .collection("announcements")
                    .document(announcement.id)
                    .delete()
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "deleteAnnouncement --> $e")
        }
    }

}