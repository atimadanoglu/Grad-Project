package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.adapter.ExpendituresListAdapter
import com.graduationproject.grad_project.model.Expenditure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


object ExpendituresOperations: FirebaseConstants() {

    private const val TAG = "ExpendituresOperations"
    private val list = MutableLiveData<ArrayList<Expenditure?>>()
    val expendituresList get() = list

    suspend fun retrieveAllExpenditures(email: String): ArrayList<Expenditure?> {
        return try {
            val documents = adminRef.document(email)
                .collection("expenditures")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .also {
                    Log.d(TAG, "retrieveAllExpenditures --> Successful!")
                }
            val expendituresList = ArrayList<Expenditure?>(arrayListOf())
            if (documents.await().documents.isNotEmpty()) {
                documents.await().documents.forEach {
                    val expenditure = Expenditure(
                        it["id"] as String,
                        it["title"] as String,
                        it["content"] as String,
                        it["amount"].toString().toLong(),
                        it["documentUri"] as String,
                        it["date"] as Timestamp
                    )
                    expendituresList.add(expenditure)
                }
            }
            expendituresList
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "retrieveAllExpenditures ---> $e")
            arrayListOf()
        }

    }


    fun retrieveExpendituresForAdmin(expenditures: MutableLiveData<MutableList<Expenditure?>>)
        = CoroutineScope(ioDispatcher).launch {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                adminRef.document(email)
                    .collection("expenditures")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveAllExpenditures --> $error")
                            return@addSnapshotListener
                        }
                        if (value?.documents?.isNotEmpty() == true) {
                            val list = mutableListOf<Expenditure?>()
                            value.documents.forEach {
                                list.add(
                                    it.toObject<Expenditure>()
                                )
                            }.also {
                                expenditures.postValue(list)
                            }
                        }
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "retrieveAllExpendituresWithSnapshot ---> $e")
        }

    }
    fun retrieveExpendituresForResident(expenditures: MutableLiveData<MutableList<Expenditure?>>)
            = CoroutineScope(ioDispatcher).launch {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                val resident = UserOperations.getResident(it)
                siteRef.document("siteName:${resident?.get("siteName")}" +
                        "-city:${resident?.get("city")}" +
                        "-district:${resident?.get("district")}")
                    .collection("expenditures")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveAllExpenditures --> $error")
                            return@addSnapshotListener
                        }
                        if (value?.documents?.isNotEmpty() == true) {
                            val list = mutableListOf<Expenditure?>()
                            value.documents.forEach { document ->
                                list.add(
                                    document.toObject<Expenditure>()
                                )
                            }.also {
                                expenditures.postValue(list)
                            }
                        }
                    }
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "retrieveAllExpendituresWithSnapshot ---> $e")
        }

    }

    fun deleteExpenditure(email: String, position: Int) {
        CoroutineScope(ioDispatcher).launch {
            try {
                list.value?.get(position)?.let {
                    adminRef.document(email)
                        .collection("expenditures")
                        .document(it.id)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }
                            value?.reference?.delete()
                            val newList = ArrayList<Expenditure?>()
                            if (list.value != null) {
                                newList.addAll(list.value!!)
                            }
                            if (newList.isNotEmpty()) {
                                newList.removeAt(position)
                                list.value?.clear()
                                list.postValue(newList)
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteExpenditure --> $e")
            }
        }
    }

    fun deleteExpenditure(expenditure: Expenditure) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = auth.currentUser?.email
                val db = FirebaseFirestore.getInstance()
                if (email != null) {
                    db.collection("administrators")
                        .document(email)
                        .collection("expenditures")
                        .document(expenditure.id)
                        .delete()
                        .await()
                }
            } catch (e: Exception) {
                Log.e(ExpendituresListAdapter.TAG, "deleteExpenditure ---> $e")
            }
        }
    }


}