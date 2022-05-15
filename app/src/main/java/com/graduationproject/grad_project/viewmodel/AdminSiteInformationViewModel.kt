package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.launch

class AdminSiteInformationViewModel: ViewModel() {

    companion object {
        private const val TAG = "AdminSiteInformationViewModel"
    }

    val inputCity = MutableLiveData("")
    val inputDistrict = MutableLiveData("")
    val inputSiteName = MutableLiveData("")
    val inputBlockName = MutableLiveData("")
    val inputFlatCount = MutableLiveData("")
    val inputMonthlyPayment = MutableLiveData("")

    private val _isThereAnyAdmin= MutableLiveData<Boolean?>()
    val isThereAnyAdmin: LiveData<Boolean?> get() = _isThereAnyAdmin

    private val _isUserCreated = MutableLiveData<Boolean?>()
    val isUserCreated: LiveData<Boolean?> get() = _isUserCreated

    private var _admin = hashMapOf<String, Any>()
    val admin get() = _admin

    private var _site = hashMapOf<String, Any>()

    fun isEmpty() = inputCity.value.isNullOrBlank() || inputSiteName.value.isNullOrBlank()
            || inputBlockName.value.isNullOrBlank() || inputDistrict.value.isNullOrBlank()
            || inputFlatCount.value.isNullOrBlank() || inputMonthlyPayment.value.isNullOrBlank()

    fun checkEmailAddress(email: String) {
        UserOperations.isThereAnyAdmin(email, _isThereAnyAdmin)
    }

    fun registerUserWithEmailAndPassword(
        email: String,
        password: String
    ) = viewModelScope.launch {
        UserOperations.createUserWithEmailAndPassword(email, password, _isUserCreated)
    }

    fun setAdminInfo(
        fullName: String,
        phoneNumber: String,
        email: String
    ) {
        OneSignalOperations.savePlayerId(admin)
        if (!areNull()) {
            _admin["fullName"] = fullName
            _admin["phoneNumber"] = phoneNumber
            _admin["email"] = email
            _admin["siteName"] = inputSiteName.value.toString()
            _admin["city"] = inputCity.value.toString()
            _admin["district"] = inputDistrict.value.toString()
            _admin["blockName"] = inputBlockName.value.toString()
            _admin["flatCount"] = inputFlatCount.value.toString().toLong()
            _admin["typeOfUser"] = "Yönetici"
            _admin["expendituresAmount"] = 0L
        }
    }

    fun areNull() = inputSiteName.value == null && inputCity.value == null
            && inputDistrict.value == null && inputBlockName.value == null && inputFlatCount.value == null
            && inputMonthlyPayment.value == null

    fun updateUserDisplayName() = viewModelScope.launch {
        UserOperations.updateFullNameForAdmin(_admin["fullName"] as String)
    }

    fun saveAdminInfoToDB() = viewModelScope.launch {
        UserOperations.saveAdminIntoDB(admin)
    }

    fun saveSiteIntoDB() = viewModelScope.launch {
        try {
            _site = convertSiteInfoHashMap()
            SiteOperations.saveSiteInfoIntoDB(_site)
        } catch (e: Exception) {
            Log.e(TAG, "saveSiteIntoDB --> $e")
        }
    }

    private fun convertSiteInfoHashMap(): HashMap<String, Any> {
        if (!areNull()) {
            return hashMapOf(
                "siteName" to inputSiteName.value.toString(),
                "city" to inputCity.value.toString(),
                "district" to inputDistrict.value.toString(),
                "blockName" to inputBlockName.value.toString(),
                "flatCount" to inputFlatCount.value.toString().toLong(),
                "monthlyPayment" to inputMonthlyPayment.value.toString().toLong(),
                "collectedMoney" to 0L
            )
        }
        return hashMapOf()
    }
}

