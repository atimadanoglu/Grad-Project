package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure

class SiteExpendituresResidentViewModel: ViewModel() {

    private val _expenditures = MutableLiveData<MutableList<Expenditure?>>()
    val expenditures: LiveData<MutableList<Expenditure?>> get() = _expenditures

    private val _expenditure = MutableLiveData<Expenditure?>()
    val expenditure: LiveData<Expenditure?> get() = _expenditure

    private val _openOptionsMenu = MutableLiveData<Boolean?>()
    val openOptionsMenu: LiveData<Boolean?> get() = _openOptionsMenu

    fun retrieveExpenditures() {
        ExpendituresOperations.retrieveExpendituresForResident(_expenditures)
    }

    fun deleteExpenditure() {
        _expenditure.value?.let {
            ExpendituresOperations.deleteExpenditure(_expenditure.value!!)
        }
    }

    fun saveInfo(expenditure: Expenditure?) {
        _expenditure.value = expenditure
        _openOptionsMenu.value = true
    }
}