package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.PaymentOperations
import com.graduationproject.grad_project.model.Payment

class CollectionOfPaymentsViewModel: ViewModel() {

    private val _payments = MutableLiveData<List<Payment?>>()
    val payments: LiveData<List<Payment?>> get() = _payments

    private val _payment = MutableLiveData<Payment?>()
    val payment: LiveData<Payment?> get() = _payment

    private val _openMenuOptions = MutableLiveData<Boolean?>()
    val openMenuOptions: LiveData<Boolean?> get() = _openMenuOptions

    fun retrievePayments() {
        PaymentOperations.retrievePayments(_payments)
    }

    fun savePaymentInfo(payment: Payment) {
        _payment.value = payment
        _openMenuOptions.value = true
    }
}