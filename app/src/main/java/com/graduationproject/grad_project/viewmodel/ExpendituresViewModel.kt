package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure

class ExpendituresViewModel: ViewModel() {

    private var _expenditures = MutableLiveData<MutableList<Expenditure?>>()
    val expenditures: LiveData<MutableList<Expenditure?>> get() = _expenditures

    private val _expenditure = MutableLiveData<Expenditure>()
    val expenditure: LiveData<Expenditure> get() = _expenditure

    fun retrieveAllExpenditures() {
        ExpendituresOperations.retrieveExpendituresForAdmin(_expenditures)
    }

    fun saveExpenditure(value: Expenditure) {
        _expenditure.value = value
    }
}