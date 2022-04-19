package com.graduationproject.grad_project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import kotlinx.coroutines.launch

class PayDebtViewModel: ViewModel() {

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

    fun setAllValid(value: Boolean) {
        _allValid.value = value
    }

    fun payDebt() {
        viewModelScope.launch {
            val email = FirebaseAuth.getInstance().currentUser?.email
            _amount.value?.toDouble()?.let { value ->
                if (email != null) {
                    UserOperations.deleteDebt(email, value)
                }
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

    fun isAllValid() {
        _allValid.value = _isValidAmount.value == true && _isValidCVC.value == true && _isValidCardNumber.value == true
                && _isValidExpirationDate.value == true
    }

}