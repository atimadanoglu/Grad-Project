package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.firebase.PaymentOperations
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Payment
import com.graduationproject.grad_project.model.SiteResident
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class PayDebtViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> get() = _amount
    private val _cardHolderName = MutableLiveData("")
    val cardHolderName: LiveData<String> get() = _cardHolderName
    private val _cardNumber = MutableLiveData("")
    val cardNumber: LiveData<String> get() = _cardNumber
    private val _expirationDate = MutableLiveData("")
    val expirationDate: LiveData<String> get() = _expirationDate
    private val _cvc = MutableLiveData(0)
    val cvc: LiveData<Int> get() = _cvc
    private val _isChecked = MutableLiveData(false)
    val isChecked: LiveData<Boolean> get() = _isChecked
    private val _allValid = MutableLiveData(false)
    val allValid: LiveData<Boolean> get() = _allValid

    private val _isValidAmount = MutableLiveData(false)
    val isValidAmount: LiveData<Boolean> get() = _isValidAmount
    private val _isValidCardNumber = MutableLiveData(false)
    val isValidCardNumber: LiveData<Boolean> get() = _isValidCardNumber
    private val _isValidExpirationDate = MutableLiveData(false)
    val isValidExpirationDate: LiveData<Boolean> get() = _isValidExpirationDate
    private val _isValidCVC = MutableLiveData(false)
    val isValidCVC: LiveData<Boolean> get() = _isValidCVC

    private val _highestAmount = MutableLiveData(0)
    val highestAmount: LiveData<Int> get() = _highestAmount

    private val _siteResident = MutableLiveData<SiteResident?>()
    val siteResident: LiveData<SiteResident?> get() = _siteResident

    fun setIsValidAmount(value: Int) {
        viewModelScope.launch {
            val email = FirebaseAuth.getInstance().currentUser?.email
            email?.let {
                val resident = UserOperations.getResident(it)
                val debt = resident?.get("debt").toString().toInt()
                if (debt >= value) {
                    _isValidAmount.value = true
                }
            }
        }
    }
    fun setIsValidExpirationDate(value: Boolean) { _isValidExpirationDate.value = value }
    fun setIsValidCardNumber(value: Boolean) { _isValidCardNumber.value = value }
    fun setIsValidCVC(value: Boolean) { _isValidCVC.value = value }
    fun setAmount(value: Int) { _amount.value = value }
    fun setIsValidAmount(value: Boolean) { _isValidAmount.value = value }

    fun setAllValid(value: Boolean) {
        _allValid.value = value
    }

    fun savePayment() {
        val uuid = UUID.randomUUID()
        val payment = Payment(
            uuid.toString(),
            _siteResident.value?.fullName!!,
            _siteResident.value?.blockNo!!,
            _siteResident.value?.flatNo!!,
            _amount.value?.toLong()!!,
            Timestamp.now()
        )
        SiteOperations.savePayment(payment)
        PaymentOperations.savePaymentIntoResidentCollection(payment)
        SiteOperations.updateCollectedMoney(payment)
    }

    fun payDebt() {
        viewModelScope.launch {
            val email = FirebaseAuth.getInstance().currentUser?.email
            _amount.value?.toDouble()?.let { value ->
                if (email != null) {
                    UserOperations.deleteDebt(email, value.toLong())
                }
            }
        }
    }

    private fun retrieveResident() = viewModelScope.launch {
        val email = FirebaseAuth.getInstance().currentUser?.email
        email?.let {
            val resident = async {
                UserOperations.getResident(email)
            }
            _siteResident.postValue(resident.await()?.toObject())
        }
    }

    init {
        retrieveResident()
    }

    fun setHighestValue() {
        viewModelScope.launch {
            val email = FirebaseAuth.getInstance().currentUser?.email
            if (email != null) {
                val resident = async {
                    UserOperations.getResident(email)
                }
                _highestAmount.value = resident.await()?.get("debt").toString().toInt()
            }
        }
    }

    fun setValues(amount: Int,
                          cardHolder: String,
                          cardNumber: String,
                          expirationDate: String,
                          cvc: Int
    ) {
        _amount.value = amount
        _cardHolderName.value = cardHolder
        _cardNumber.value = cardNumber
        _expirationDate.value = expirationDate
        _cvc.value = cvc
    }

}