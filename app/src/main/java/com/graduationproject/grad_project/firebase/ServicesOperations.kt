package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
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


    fun retrieveServicesForAdmin(services: MutableLiveData<MutableList<Service?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val admin = currentUser?.email?.let { UserOperations.getAdmin(it) }
            currentUser?.email?.let {
                siteRef
                    .document("siteName:${admin?.get("siteName")}" +
                            "-city:${admin?.get("city")}" +
                            "-district:${admin?.get("district")}")
                    .collection("services")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveServices --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedServices = mutableListOf<Service?>()
                        documents?.forEach {
                            retrievedServices.add(
                                it.toObject<Service>()
                            )
                        }.also {
                            services.postValue(retrievedServices)
                        }
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "addService --> $e")
        }
    }


    fun retrieveServicesForResident(services: MutableLiveData<MutableList<Service?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val resident = currentUser?.email?.let { UserOperations.getResident(it) }
            currentUser?.email?.let {
                siteRef
                    .document("siteName:${resident?.get("siteName")}" +
                            "-city:${resident?.get("city")}" +
                            "-district:${resident?.get("district")}")
                    .collection("services")
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveServices --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedServices = mutableListOf<Service?>()
                        documents?.forEach {
                            retrievedServices.add(
                                it.toObject<Service>()
                            )
                        }.also {
                            services.postValue(retrievedServices)
                        }
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "addService --> $e")
        }
    }

}