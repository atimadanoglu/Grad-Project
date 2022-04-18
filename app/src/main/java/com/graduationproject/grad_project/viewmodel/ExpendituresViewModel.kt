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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ExpendituresViewModel: ViewModel() {

    companion object {
        const val TAG = "ExpendituresViewModel"
    }
    private var _expenditures = MutableLiveData<ArrayList<Expenditure?>>()
    val expenditures: LiveData<ArrayList<Expenditure?>> get() = _expenditures

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val adminRef: CollectionReference by lazy {
        db.collection("administrators")
    }

    /*suspend fun getExpenditures() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            _expenditures.value = ExpendituresOperations.retrieveAllExpendituresWithSnapshot(email)
        }
    }*/

    fun getExpenditures() {
        viewModelScope.launch {
            val email = async {
                FirebaseAuth.getInstance().currentUser?.email
            }
            if (email.await() != null) {
                val job = async {
                    ExpendituresOperations.retrieveAllExpendituresWithSnapshot(email.await()!!)
                }
                job.await()
                if (job.isCompleted) {
                    _expenditures = ExpendituresOperations.expendituresList
                }
            }
        }
    }

    fun retrieveAllExpendituresWithSnapshot(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                adminRef.document(email)
                    .collection("expenditures")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG,"retrieveAllExpenditures --> $error")
                            return@addSnapshotListener
                        }
                        if (value?.documents?.isNotEmpty() == true) {
                            val newArrayList = ArrayList<Expenditure?>(arrayListOf())
                            value.documents.forEach {
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
                    }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "retrieveAllExpendituresWithSnapshot ---> $e")
            }
        }
    }

}