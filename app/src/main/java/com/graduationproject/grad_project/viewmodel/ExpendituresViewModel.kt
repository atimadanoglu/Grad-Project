package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ExpendituresViewModel: ViewModel() {

    private var _expenditures = MutableLiveData<ArrayList<Expenditure?>>()
    val expenditures: LiveData<ArrayList<Expenditure?>> get() = _expenditures

  /*  suspend fun getExpenditures() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            _expenditures.value = ExpendituresOperations.retrieveAllExpenditures(email)
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

}