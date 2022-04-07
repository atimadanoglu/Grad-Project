package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.coroutineContext

class AdminSiteInformationViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        private const val TAG = "AdminSiteInformationViewModel"
    }
    private lateinit var externalScope: CoroutineScope
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
    ): Boolean {
        return withContext(scope) {
            try {
                val data = async(scope) {
                    UserOperations
                        .createUserWithEmailAndPassword(email, password)?.user
                }
                val user = data.await()
                user?.let { saveAdminUid(it.uid) }
                UserOperations.loginWithEmailAndPassword(email, password)
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
                _admin["typeOfUser"] = "Yönetici"
                true
            } catch (e: Exception) {
                Log.e(TAG, "createAdmin ---> $e")
                false
            }
        }
    }

    suspend fun updateUserDisplayName() {
        UserOperations.updateUserInfo(_admin)
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

    suspend fun createAndSaveAdminAndSiteIntoDB(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String
    ) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val isWritten = async {
                    saveAdminIntoDB(fullName, phoneNumber, email, password)
                }
                saveSiteIntoDB()
            } catch (e: Exception) {
                Log.e(TAG, "saveAdminAndSiteInfoIntoDB ---> $e")
            }
        }.join()
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

