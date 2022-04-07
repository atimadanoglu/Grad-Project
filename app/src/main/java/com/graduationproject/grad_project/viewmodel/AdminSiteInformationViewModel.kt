package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.lang.Exception

class AdminSiteInformationViewModel: ViewModel() {

    companion object {
        private const val TAG = "AdminSiteInformationViewModel"
    }
    private var _siteName = ""
    val siteName get() = _siteName

    private var _city = ""
    val city get() = _city

    private var _district = ""
    val district get() = _district

    private var _blockCount = ""
    val blockCount get() = _blockCount

    private var _flatCount = 0
    val flatCount get() = _flatCount

    private var _admin = hashMapOf<String, Any>()
    val admin get() = _admin

    private var _site = hashMapOf<String, Any>()
    val site get() = _site

    fun setSiteName(siteName: String) { _siteName = siteName }
    fun setCity(city: String) { _city = city }
    fun setDistrict(district: String) { _district = district }
    fun setBlockCount(blockCount: String) { _blockCount = blockCount }
    fun setFlatCount(flatCount: Int) { _flatCount = flatCount }

    private suspend fun createAdmin(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        scope: CoroutineDispatcher = Dispatchers.IO
    ): HashMap<String, Any> {
        return withContext(scope) {
            try {
                val uid = async(scope) {
                    UserOperations
                        .createUserWithEmailAndPassword(email, password)?.user?.uid
                }
                uid.await()?.let { saveAdminUid(it) }
                OneSignalOperations.savePlayerId(_admin)

                _admin["fullName"] = fullName
                _admin["phoneNumber"] = phoneNumber
                _admin["email"] = email
                _admin["password"] = password
                _admin["siteName"] = siteName
                _admin["city"] = city
                _admin["district"] = district
                _admin["blockCount"] = blockCount
                _admin["flatCount"] = flatCount
                _admin
            } catch (e: Exception) {
                Log.e(TAG, "createAdmin ---> $e")
                hashMapOf()
            }
        }
    }

    private fun saveAdminIntoDB(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        scope: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(scope) {
            try {
                createAdmin(fullName, phoneNumber, email, password)
                UserOperations.saveAdminIntoDB(_admin)
            } catch (e: Exception) {
                Log.e(TAG, "saveAdminIntoDB ---> $e")
            }
        }
    }

    private fun saveSiteIntoDB(scope: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(scope) {
            try {
                val siteHashMap = async {
                    convertSiteInfoHashMap()
                }
                _site = siteHashMap.await()
                launch(scope) {
                    SiteOperations.saveSiteInfoIntoDB(_site)
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveSiteIntoDB --> $e")
            }
        }
    }

    fun saveAdminAndSiteInfoIntoDB(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
        scope: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(scope) {
            try {
                saveAdminIntoDB(fullName, phoneNumber, email, password)
                saveSiteIntoDB()
            } catch (e: Exception) {
                Log.e(TAG, "saveAdminAndSiteInfoIntoDB ---> $e")
            }
        }
    }

    private fun saveAdminUid(uid: String){
        _admin["uid"] = uid
    }

    private fun convertSiteInfoHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "siteName" to siteName,
            "city" to city,
            "district" to district,
            "blockCount" to blockCount,
            "flatCount" to flatCount,
        )
    }
}

