package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.model.Payment

class CollectionOfPaymentsViewModel: ViewModel() {

    private val _payments = MutableLiveData<List<Payment?>>()
    val payments: LiveData<List<Payment?>> get() = _payments

    private val _payment = MutableLiveData<Payment?>()
    val payment: LiveData<Payment?> get() = _payment

    fun retrievePayments() {
        SiteOperations.retrievePayments(_payments)
    }
}