package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure
import kotlinx.coroutines.*

class ExpendituresViewModel: ViewModel() {

    companion object {
        const val TAG = "ExpendituresViewModel"
    }
    private var _expenditures = MutableLiveData<ArrayList<Expenditure?>>(arrayListOf())
    val expenditures: LiveData<ArrayList<Expenditure?>> get() = _expenditures

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val adminRef: CollectionReference by lazy {
        db.collection("administrators")
    }

    fun retrieveAllExpendituresWithSnapshot() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = async {
                    FirebaseAuth.getInstance().currentUser?.email
                }
                adminRef.document(requireNotNull(email.await()))
                    .collection("expenditures")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG,"retrieveAllExpenditures --> $error")
                            return@addSnapshotListener
                        }
                        val newArrayList = ArrayList<Expenditure?>(arrayListOf())
                        value?.documents?.forEach {
                            val expenditure = Expenditure(
                                it["id"] as String,
                                it["title"] as String,
                                it["content"] as String,
                                it["amount"].toString().toLong().toInt(),
                                it["documentUri"] as String,
                                it["date"] as Timestamp
                            )
                            newArrayList.add(expenditure)
                        }.also {
                            _expenditures.value = newArrayList
                        }
                    }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "retrieveAllExpendituresWithSnapshot ---> $e")
            }
        }
    }

}