package com.graduationproject.grad_project.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.model.SiteResident
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResidentsListViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "ResidentsListViewModel"
    }
    private val _residentsList = MutableLiveData<MutableList<SiteResident?>>(arrayListOf())
    val residentsList: LiveData<MutableList<SiteResident?>> get() = _residentsList

  /*  fun filter(newText: String?) {
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

    }*/

    private fun contains(newText: String?, resident: SiteResident): Boolean {
        return resident.fullName.contains(newText.toString(), true)
                || resident.flatNo.toString().contains(newText.toString())
                || resident.blockNo.contains(newText.toString(), true)
    }

    fun getResidentsInASpecificSiteWithSnapshot() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val db = FirebaseFirestore.getInstance()
                val auth = FirebaseAuth.getInstance()
                val adminEmail = auth.currentUser?.email.toString()

                val adminInfo = async {
                    db.collection("administrators")
                        .document(adminEmail)
                        .get().await()
                }
                val query = db.collection("residents")
                    .whereEqualTo("city", adminInfo.await().get("city"))
                    .whereEqualTo("district", adminInfo.await().get("district"))
                    .whereEqualTo("siteName", adminInfo.await().get("siteName"))
                    .whereEqualTo("isVerified", true)

                query.addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "getResidentsInASpecificSiteWithSnapshot --> $error")
                        return@addSnapshotListener
                    }
                    val documents = value?.documents
                    val residents = mutableListOf<SiteResident?>()
                    documents?.sortBy {
                        it["flatNo"].toString().toInt()
                    }
                    documents?.forEach {
                        residents.add(it.toObject<SiteResident>())
                    }.also {
                        _residentsList.postValue(residents)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "getResidentsInASpecificSiteWithSnapshot --> $e")
            }
        }
    }
}