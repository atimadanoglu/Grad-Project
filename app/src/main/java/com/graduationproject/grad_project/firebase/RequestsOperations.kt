package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.graduationproject.grad_project.model.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

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

    fun deleteRequestFromResidentDB(email: String, request: Request) {
        CoroutineScope(ioDispatcher + coroutineExceptionHandler).launch {
            try {
                residentRef.document(email)
                    .collection("requests")
                    .document(request.id)
                    .delete()
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "deleteRequestFromResidentDB ---> $e")
            }
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

    suspend fun getRequestsOfResidentsThemselves(email: String): ArrayList<Request?> {
        return withContext(ioDispatcher) {
            try {
                val requests = arrayListOf<Request?>()
                val querySnapshot = residentRef.document(email)
                    .collection("requests")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                querySnapshot.documents.forEach {
                    requests.add(
                        Request(
                            it.get("id").toString(),
                            it.get("title").toString(),
                            it.get("content").toString(),
                            it.get("type").toString(),
                            it.get("sentBy").toString(),
                            it.get("date") as Timestamp
                        )
                    )
                }
                requests
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "getRequestsOfResidentsThemselves ---> $e")
                arrayListOf()
            }
        }
    }

}