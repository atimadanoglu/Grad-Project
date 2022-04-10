package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.SiteResident
import kotlinx.coroutines.*

class ResidentsListViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    companion object {
        const val TAG = "ResidentsListViewModel"
    }

    private val _residentsList = MutableLiveData<ArrayList<SiteResident?>>(arrayListOf())
    val residentsList get() = _residentsList

    private val _filteredList = MutableLiveData<ArrayList<SiteResident?>>()
    val filteredList get() = _filteredList

    fun filter(newText: String?) {
        viewModelScope.launch(mainDispatcher) {
            val list = _residentsList.value
            list?.let {
                for (resident in list) {
                    resident?.let {
                        if (contains(newText, it))
                            _filteredList.value?.add(it)
                    }
                }
            }
        }

    }

    private fun contains(newText: String?, resident: SiteResident): Boolean {
        return resident.fullName.contains(newText.toString(), true)
                || resident.flatNo.toString().contains(newText.toString())
                || resident.blockNo.contains(newText.toString(), true)
    }

    fun retrieveAndShowResidents() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                val admin = async {
                    email?.let { UserOperations.getAdmin(it) }
                }
                admin.await()?.let {
                    Log.d(TAG, "admin.await() is not null")
                    createResidentsList(admin.await()!!)
                }
            } catch (e: Exception) {
                Log.e(TAG, "retrieveAndShowResidents ---> $e")
            }
        }
    }

    private suspend fun createResidentsList(admin: DocumentSnapshot) {
        val residents = UserOperations.getResidentsInASpecificSite(admin)
        val arrayList = arrayListOf<SiteResident?>()
        withContext(Dispatchers.Main) {
            residents?.documents?.forEach {
                arrayList.add(
                    SiteResident(
                        it["fullName"].toString(),
                        it["email"].toString(),
                        it["phoneNumber"].toString(),
                        it["blockNo"].toString(),
                        it["flatNo"].toString().toInt(),
                        it["debt"].toString().toDouble(),
                        it["player_id"].toString(),
                        it["typeOfUser"].toString(),
                        it["siteName"].toString(),
                        it["city"].toString(),
                        it["district"].toString()
                    )
                )
            }.apply {
                _residentsList.value = arrayList
            }
        }
    }

}