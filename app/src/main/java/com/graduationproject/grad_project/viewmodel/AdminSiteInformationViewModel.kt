package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.lang.Exception

class AdminSiteInformationViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        private const val TAG = "AdminSiteInformationViewModel"
    }

    val inputCity = MutableLiveData("")
    val inputDistrict = MutableLiveData("")
    val inputSiteName = MutableLiveData("")
    val inputBlockName = MutableLiveData("")
    val inputFlatCount = MutableLiveData("")
    val inputMonthlyPayment = MutableLiveData("")

    private var _admin = hashMapOf<String, Any>()
    val admin get() = _admin

    private var _site = hashMapOf<String, Any>()

    private suspend fun createAdmin(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        scope: CoroutineDispatcher = Dispatchers.IO
    ): Boolean {
        return withContext(scope) {
            try {
                val data = async(scope) {
                    UserOperations
                        .createUserWithEmailAndPassword(email, password)?.user
                }
                val user = data.await()
                user?.let { saveAdminUid(it.uid) }
                OneSignalOperations.savePlayerId(_admin)

                if (!areNull()) {
                    _admin["fullName"] = fullName
                    _admin["phoneNumber"] = phoneNumber
                    _admin["email"] = email
                    _admin["password"] = password
                    _admin["siteName"] = inputSiteName.value.toString()
                    _admin["city"] = inputCity.value.toString()
                    _admin["district"] = inputDistrict.value.toString()
                    _admin["blockName"] = inputBlockName.value.toString()
                    _admin["flatCount"] = inputFlatCount.value.toString().toLong()
                    _admin["typeOfUser"] = "Yönetici"
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "createAdmin ---> $e")
                false
            }
        }
    }

    fun areNull() = inputSiteName.value == null && inputCity.value == null
            && inputDistrict.value == null && inputBlockName.value == null && inputFlatCount.value == null
            && inputMonthlyPayment.value == null

    suspend fun updateUserDisplayName() {
        UserOperations.updateFullNameForAdmin(_admin["fullName"] as String)
    }

    suspend fun saveAdminIntoDB(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String
    ): Boolean {
        return withContext(ioDispatcher) {
            try {
                val isCreated = async {
                    createAdmin(fullName, phoneNumber, email, password)
                }
                if (isCreated.await()) {
                    UserOperations.saveAdminIntoDB(_admin)
                    return@withContext true
                }
                false
            } catch (e: Exception) {
                Log.e(TAG, "saveAdminIntoDB ---> $e")
                false
            }
        }
    }

    fun saveSiteIntoDB() {
        CoroutineScope(ioDispatcher).launch {
            try {
                val siteHashMap = async {
                    convertSiteInfoHashMap()
                }
                _site = siteHashMap.await()
                SiteOperations.saveSiteInfoIntoDB(_site)
            } catch (e: Exception) {
                Log.e(TAG, "saveSiteIntoDB --> $e")
            }
        }
    }

    private fun saveAdminUid(uid: String){
        _admin["uid"] = uid
    }

    private fun convertSiteInfoHashMap(): HashMap<String, Any> {
        if (!areNull()) {
            return hashMapOf(
                "siteName" to inputSiteName.value.toString(),
                "city" to inputCity.value.toString(),
                "district" to inputDistrict.value.toString(),
                "blockName" to inputBlockName.value.toString(),
                "flatCount" to inputFlatCount.value.toString().toLong(),
                "monthlyPayment" to inputMonthlyPayment.value.toString().toLong()
            )
        }
        return hashMapOf()
    }
}

