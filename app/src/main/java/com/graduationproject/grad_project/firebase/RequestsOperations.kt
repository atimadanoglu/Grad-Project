package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object RequestsOperations: FirebaseConstants() {

    const val TAG = "RequestsOperations"

    fun saveRequestIntoResidentDB(email: String, request: Request) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                residentRef.document(email)
                    .collection("requests")
                    .document(request.id)
                    .set(request)
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveRequestIntoResidentDB --> $e")
            }
        }
    }

    fun deleteRequestFromResidentDB(request: Request) =
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
        try {
            currentUserEmail?.let {
                residentRef.document(it)
                    .collection("requests")
                    .document(request.id)
                    .delete()
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "deleteRequestFromResidentDB ---> $e")
        }
    }


    fun saveRequestIntoAdminDB(email: String, request: Request) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                adminRef.document(email)
                    .collection("requests")
                    .document(request.id)
                    .set(request)
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveRequestIntoAdminDB")
            }
        }
    }

    fun saveRequestIntoNotificationCollection(notification: Notification) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                val resident = UserOperations.getResident(it)
                adminRef.whereEqualTo("siteName", resident?.get("siteName").toString())
                    .whereEqualTo("city", resident?.get("city").toString())
                    .whereEqualTo("district", resident?.get("district").toString())
                    .get().await().also { document ->
                        adminRef.document(document?.documents?.get(0)?.get("email").toString())
                            .collection("notifications")
                            .document(notification.id)
                            .set(notification)
                            .await()
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "saveRequestIntoNotificationCollection --> $e")
        }
    }

    fun deleteRequestFromAdminDB(email: String, request: Request) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                adminRef.document(email)
                    .collection("requests")
                    .document(request.id)
                    .delete()
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "deleteRequestFromAdminDB")
            }
        }
    }

    suspend fun getAllRequestsForAdmin(email: String): ArrayList<Request> {
        return withContext(ioDispatcher + coroutineExceptionHandler) {
            try {
                val requests = ArrayList<Request>()
                adminRef.document(email)
                    .collection("requests")
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }
                        value?.forEach {
                            requests.add(
                                Request(
                                    it.get("id").toString(),
                                    it.get("title").toString(),
                                    it.get("content").toString(),
                                    it.get("type").toString(),
                                    it.get("documentUri").toString(),
                                    it.get("sentBy").toString(),
                                    it.get("date") as Timestamp
                                )
                            )
                        }
                    }
                requests
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "getAllRequests --> $e" )
                arrayListOf()
            }
        }
    }

    fun retrieveRequestsForResident(requests: MutableLiveData<ArrayList<Request?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            currentUserEmail?.let {
                residentRef.document(it)
                    .collection("requests")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveRequestsForResident --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedRequests = arrayListOf<Request?>()
                        documents?.forEach { document ->
                            retrievedRequests.add(
                                document.toObject<Request>()
                            )
                        }.also {
                            requests.value = retrievedRequests
                        }
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "retrieveRequestsForResident --> $e")
        }
    }

}