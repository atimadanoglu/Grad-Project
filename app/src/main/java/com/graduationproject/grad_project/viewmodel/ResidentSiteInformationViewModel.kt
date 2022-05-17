package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.grad_project.firebase.SiteOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.RegistrationStatus
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
    val inputBlockName = MutableLiveData("")
    val inputFlatNo = MutableLiveData("")

    private var _resident = hashMapOf<String, Any>()
    val resident get() = _resident

    private val _allSites = MutableLiveData<MutableList<Site?>>()
    val allSites: LiveData<MutableList<Site?>> get() = _allSites

    private val _allSiteNames = mutableListOf<String?>()
    val allSiteNames: MutableList<String?> get() = _allSiteNames

    private val _blockNames = MutableLiveData<List<String?>>()
    val blockNames: LiveData<List<String?>> get() = _blockNames

    private val _listOfBlocks = mutableListOf<String?>()
    val listOfBlocks: MutableList<String?> get() = _listOfBlocks

    private val _totalFlatCount = MutableLiveData<Long?>()
    val totalFlatCount: LiveData<Long?> get() = _totalFlatCount

    private val _flatList = MutableLiveData<MutableList<Long?>>()
    val flatList: LiveData<MutableList<Long?>> get() = _flatList

    private val _isThereAnyResident = MutableLiveData<Boolean?>()
    val isThereAnyResident: LiveData<Boolean?> get() = _isThereAnyResident

    fun clearLists() {
        _allSiteNames.clear()
        _flatList.value?.clear()
        _listOfBlocks.clear()
    }

    fun retrieveAllSitesBasedOnCityAndDistrict() {
        if (inputCity.value != null && inputDistrict.value != null) {
            _allSites.value?.clear()
            _allSiteNames.clear()
            SiteOperations
                .retrieveAllSitesBasedOnCityAndDistrict(inputCity.value!!, inputDistrict.value!!, _allSites)
        }
    }

    fun getSiteNames() {
        _allSiteNames.clear()
        _allSites.value?.forEach {
            it?.let { site ->
                _allSiteNames.add(site.siteName)
            }
        }
    }

    fun getFlats() {
        UserOperations.takeFlatNumbers(
            inputSiteName.value!!, inputCity.value!!, inputDistrict.value!!, _totalFlatCount.value!!, _flatList
        )
    }

    fun retrieveBlockNameAndFlatCount() {
        if (!areTheyNull()) {
            SiteOperations.retrieveBlockNamesBasedOnSiteInfo(
                inputSiteName.value!!, inputCity.value!!, inputDistrict.value!!, _blockNames, _totalFlatCount
            )
        }
    }

    fun isEmpty() = inputCity.value.isNullOrBlank() || inputSiteName.value.isNullOrBlank()
            || inputBlockName.value.isNullOrBlank() || inputDistrict.value.isNullOrBlank()
            || inputFlatNo.value.isNullOrBlank()

    /**
     * It can be used to check these items are null before getting block names
     * */
    private fun areTheyNull() = inputSiteName.value == null || inputCity.value == null
            || inputDistrict.value == null

    fun getBlockNames() {
        _blockNames.value?.forEach {
            it?.let {
                _listOfBlocks.add(it)
            }
        }
    }

    fun checkEmailAddress(email: String) {
        UserOperations.isThereAnyResident(email, _isThereAnyResident)
    }

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

                if (!areNull()) {
                    _resident["fullName"] = fullName
                    _resident["phoneNumber"] = phoneNumber
                    _resident["email"] = email
                    _resident["siteName"] = inputSiteName.value.toString()
                    _resident["city"] = inputCity.value.toString()
                    _resident["district"] = inputDistrict.value.toString()
                    _resident["blockNo"] = inputBlockName.value.toString()
                    _resident["flatNo"] = inputFlatNo.value.toString().toLong()
                    _resident["typeOfUser"] = "Sakin"
                    _resident["registrationStatus"] = RegistrationStatus.PENDING
                    _resident["debt"] = 0L
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "createResident ---> $e")
                false
            }
        }
    }
    private fun areNull() = inputSiteName.value == null && inputCity.value == null
            && inputDistrict.value == null && inputBlockName.value == null && inputFlatNo.value == null

   /* suspend fun updateUserDisplayName() {
        UserOperations.updateFullNameForResident(_resident["fullName"] as String)
    }*/

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