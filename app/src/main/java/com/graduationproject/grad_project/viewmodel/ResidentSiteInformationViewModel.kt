package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.lang.Exception

class ResidentSiteInformationViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        private const val TAG = "ResidentSiteInformationViewModel"
    }

    private var _siteName = ""
    val siteName get() = _siteName

    private var _city = ""
    val city get() = _city

    private var _district = ""
    val district get() = _district

    private var _blockNo = ""
    val blockNo get() = _blockNo

    private var _flatNo = 0
    val flatNo get() = _flatNo

    private var _resident = hashMapOf<String, Any>()
    val resident get() = _resident

    private var _site = hashMapOf<String, Any>()
    val site get() = _site

    fun setSiteName(siteName: String) { _siteName = siteName }
    fun setCity(city: String) { _city = city }
    fun setDistrict(district: String) { _district = district }
    fun setBlockNo(blockNo: String) { _blockNo = blockNo }
    fun setFlatNo(flatNo: Int) { _flatNo = flatNo }


    private suspend fun createResident(
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
                user?.let { saveResidentUid(it.uid) }
/*
                UserOperations.loginWithEmailAndPassword(email, password)
*/
                OneSignalOperations.savePlayerId(_resident)

                _resident["fullName"] = fullName
                _resident["phoneNumber"] = phoneNumber
                _resident["email"] = email
                _resident["password"] = password
                _resident["siteName"] = siteName
                _resident["city"] = city
                _resident["district"] = district
                _resident["blockNo"] = blockNo
                _resident["flatNo"] = flatNo
                _resident["typeOfUser"] = "Sakin"
                true
            } catch (e: Exception) {
                Log.e(TAG, "createResident ---> $e")
                false
            }
        }
    }

    private fun saveResidentUid(uid: String){
        _resident["uid"] = uid
    }

    suspend fun updateUserDisplayName() {
        UserOperations.updateUserInfo(_resident)
    }

    suspend fun saveResidentIntoDB(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String
    ): Boolean {
        return withContext(ioDispatcher) {
            try {
                val isCreated = async {
                    createResident(fullName, phoneNumber, email, password)
                }
                if (isCreated.await()) {
                    UserOperations.saveResidentIntoDB(_resident)
                    return@withContext true
                }
                false
            } catch (e: Exception) {
                Log.e(TAG, "saveResidentIntoDB ---> $e")
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

    private fun convertSiteInfoHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "siteName" to siteName,
            "city" to city,
            "district" to district,
            "blockNo" to blockNo,
            "flatNo" to flatNo,
        )
    }



}