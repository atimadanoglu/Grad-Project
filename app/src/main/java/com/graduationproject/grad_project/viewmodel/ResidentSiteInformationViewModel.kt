package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Site
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class ResidentSiteInformationViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        private const val TAG = "ResidentSiteInformationViewModel"
    }

    val inputCity = MutableLiveData("")
    val inputDistrict = MutableLiveData("")
    val inputSiteName = MutableLiveData("")
    val inputBlockNo = MutableLiveData("")
    val inputFlatNo = MutableLiveData("")

    private var _resident = hashMapOf<String, Any>()
    val resident get() = _resident

    private val _allSites = MutableLiveData<MutableList<Site?>>()
    val allSites: LiveData<MutableList<Site?>> get() = _allSites

    private val _allSiteNames = mutableListOf<String?>()
    val allSiteNames: MutableList<String?> get() = _allSiteNames

    fun retrieveAllSitesBasedOnCityAndDistrict(city: String, district: String) {
        SiteOperations.retrieveAllSitesBasedOnCityAndDistrict(city, district, _allSites)
    }

    fun getSiteNames() {
        println("_allSites size : ${_allSites.value?.size}")
        _allSites.value?.forEach {
            it?.let { site ->
                println("site name -> ${it.siteName}")
                _allSiteNames.add(site.siteName)
                println("sonra size: ${_allSiteNames.size}")
            }
        }
    }

    val cityAndDistricts = hashMapOf(
        "İzmir" to mutableListOf("Karşıyaka", "Konak", "Bayraklı", "Çiğli", "Buca"),
        "İstanbul" to mutableListOf("Beşiktaş", "Kadıköy", "Şişli", "Bağcılar"),
        "Ankara" to mutableListOf("Akyurt", "Altındağ", "Beypazarı", "Çankaya")
    )

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
                data.await()
                OneSignalOperations.savePlayerId(_resident)

                _resident["fullName"] = fullName
                _resident["phoneNumber"] = phoneNumber
                _resident["email"] = email
                _resident["siteName"] = inputSiteName.value.toString()
                _resident["city"] = inputCity.value.toString()
                _resident["district"] = inputDistrict.value.toString()
                _resident["blockNo"] = inputBlockNo.value.toString()
                _resident["flatNo"] = inputFlatNo.value.toString().toLong()
                _resident["typeOfUser"] = "Sakin"
                _resident["isVerified"] = false
                _resident["debt"] = 0L
                true
            } catch (e: Exception) {
                Log.e(TAG, "createResident ---> $e")
                false
            }
        }
    }

    suspend fun updateUserDisplayName() {
        UserOperations.updateFullNameForResident(_resident["fullName"] as String)
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
}