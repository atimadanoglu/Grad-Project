package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.graduationproject.grad_project.model.Expenditure
import kotlinx.coroutines.tasks.await
import java.util.*

object ExpendituresOperations: FirebaseConstants() {

    private const val TAG = "ExpendituresOperations"
    private val _expendituresList = MutableLiveData<ArrayList<Expenditure?>>(arrayListOf())
    val expendituresList get() = _expendituresList

    suspend fun retrieveAllExpenditures(email: String): ArrayList<Expenditure?> {
        return try {
            val documents = adminRef.document(email)
                .collection("expenditures")
                .get()
                .also {
                    Log.d(TAG, "retrieveAllExpenditures --> Successful!")
                }
            val expendituresList = ArrayList<Expenditure?>(arrayListOf())
            if (documents.await().documents.isNotEmpty()) {
                documents.await().documents.forEach {
                    val uuid = UUID.randomUUID()
                    val expenditure = Expenditure(
                        uuid.toString(),
                        it["title"] as String,
                        it["content"] as String,
                        it["amount"].toString().toLong().toInt(),
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


    fun retrieveAllExpendituresWithSnapshot(email: String) {
        try {
            adminRef.document(email)
                .collection("expenditures")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "retrieveAllExpenditures --> $error")
                        return@addSnapshotListener
                    }
                    if (value?.documents?.isNotEmpty() == true) {
                        val newArrayList = ArrayList<Expenditure?>(arrayListOf())
                        value.documents.forEach {
                            val uuid = UUID.randomUUID()
                            val expenditure = Expenditure(
                                uuid.toString(),
                                it["title"] as String,
                                it["content"] as String,
                                it["amount"].toString().toLong().toInt(),
                                it["documentUri"] as String,
                                it["date"] as Timestamp
                            )
                            newArrayList.add(expenditure)
                        }.also {
                            _expendituresList.value = newArrayList
                        }
                    }
                }
         /*   if (documents.await().documents.isNotEmpty()) {
                documents.await().documents.forEach {
                    val uuid = UUID.randomUUID()
                    val expenditure = Expenditure(
                        uuid.toString(),
                        it["title"] as String,
                        it["content"] as String,
                        it["amount"].toString().toLong().toInt(),
                        it["documentUri"] as String,
                        it["date"] as Timestamp
                    )
                    expendituresList.add(expenditure)
                }
            }*/
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "retrieveAllExpenditures ---> $e")
        }

    }


}