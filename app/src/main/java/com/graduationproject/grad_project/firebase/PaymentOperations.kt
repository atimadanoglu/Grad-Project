package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.graduationproject.grad_project.model.Administrator
import com.graduationproject.grad_project.model.Payment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object PaymentOperations: FirebaseConstants() {

    private const val TAG = "PaymentOperations"

    fun savePaymentIntoResidentCollection(payment: Payment) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                residentRef.document(it).collection("payments")
                    .document(payment.id)
                    .set(payment)
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "savePaymentIntoResidentCollection --> $e")
        }
    }

    fun savePaymentIntoPaymentCollection(payment: Payment) = CoroutineScope(ioDispatcher).launch {
        try {
            paymentRef.document(payment.id)
                .set(payment)
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "savePaymentIntoPaymentCollection --> $e")
        }
    }

    fun retrievePayments(payments: MutableLiveData<List<Payment?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                val operation = async {
                    UserOperations.getAdmin(it)
                }
                val admin = operation.await()?.toObject<Administrator>()
                paymentRef.whereEqualTo("siteName", admin?.siteName)
                    .whereEqualTo("city", admin?.city)
                    .whereEqualTo("district", admin?.district)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrievePayments --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.toObjects<Payment>()
                        documents?.sortedByDescending {
                            it.date
                        }
                        if (documents?.isNotEmpty() == true) {
                            payments.postValue(documents)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrievePayments --> $e")
        }
    }

}