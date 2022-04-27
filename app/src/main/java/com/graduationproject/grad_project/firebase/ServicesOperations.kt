package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.model.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object ServicesOperations: FirebaseConstants() {

    const val TAG = "ServicesOperations"

    fun addService(service: Service) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val admin = currentUser?.email?.let { UserOperations.getAdmin(it) }
            currentUser?.email?.let {
                siteRef
                    .document("siteName:${admin?.get("siteName")}" +
                        "-city:${admin?.get("city")}" +
                        "-district:${admin?.get("district")}")
                    .collection("services")
                    .document(service.id)
                    .set(service)
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "addService --> $e")
        }
    }

}