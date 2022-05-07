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
    private val _residentsList = MutableLiveData<List<SiteResident?>>()
    val residentsList: LiveData<List<SiteResident?>> get() = _residentsList

    private var copiedData = listOf<SiteResident?>()

    fun filter(newText: String?) {
        viewModelScope.launch {
            val newList = copiedData
            if (newText.isNullOrEmpty()) {
                _residentsList.value = copiedData
            }
            val filteredList = newList.filter {
                it?.let { siteResident ->
                    contains(newText, siteResident)
                } == true
            }
            _residentsList.value = filteredList
        }
    }

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
                        copiedData = residents
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "getResidentsInASpecificSiteWithSnapshot --> $e")
            }
        }
    }
}