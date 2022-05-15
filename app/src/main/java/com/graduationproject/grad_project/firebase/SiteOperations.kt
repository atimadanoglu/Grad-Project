package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.graduationproject.grad_project.model.Expenditure
import com.graduationproject.grad_project.model.Meeting
import com.graduationproject.grad_project.model.Site
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

object SiteOperations: FirebaseConstants() {

    private const val TAG = "SiteOperations"
    private val scope: CoroutineDispatcher = Dispatchers.IO

    suspend fun saveSiteInfoIntoDB(
        site: HashMap<String, Any>
    ) {
        CoroutineScope(scope + coroutineExceptionHandler).launch {
            try {
                siteRef
                    .document("siteName:${site["siteName"]}-city:${site["city"]}-district:${site["district"]}")
                    .set(site).await()
            } catch (e: FirebaseException) {
                Log.e(TAG, "saveSiteInfoDB --> $e")
            }
        }
    }

    fun retrieveMeetings(meetings: MutableLiveData<List<Meeting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = auth.currentUser?.email
            val admin = async {
                email?.let { UserOperations.getAdmin(it) }
            }
            siteRef.document("siteName:${admin.await()?.get("siteName")}" +
                    "-city:${admin.await()?.get("city")}" +
                    "-district:${admin.await()?.get("district")}")
                .collection("meetings")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "retrieveMeetings --> $error")
                        return@addSnapshotListener
                    }
                    value?.toObjects<Meeting>().also {
                        meetings.postValue(it)
                    }
                }
        } catch (e: FirebaseException) {
            Log.e(TAG, "saveMeetingInfo --> $e")
        }
    }


    fun retrieveMeeting(meeting: MutableLiveData<Meeting?>) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = auth.currentUser?.email
            val admin = async {
                email?.let { UserOperations.getAdmin(it) }
            }
            siteRef.document("siteName:${admin.await()?.get("siteName")}" +
                    "-city:${admin.await()?.get("city")}" +
                    "-district:${admin.await()?.get("district")}")
                .collection("meetings")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "retrieveMeetings --> $error")
                        return@addSnapshotListener
                    }
                    if (value?.documents?.isNotEmpty() == true) {
                        value.documents[0]?.toObject<Meeting?>().also {
                            meeting.postValue(it)
                        }
                    }
                }
        } catch (e: FirebaseException) {
            Log.e(TAG, "saveMeetingInfo --> $e")
        }
    }

    fun retrieveMeetingForResident(meeting: MutableLiveData<Meeting?>) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = auth.currentUser?.email
            val resident = async {
                email?.let { UserOperations.getResident(it) }
            }
            siteRef.document("siteName:${resident.await()?.get("siteName")}" +
                    "-city:${resident.await()?.get("city")}" +
                    "-district:${resident.await()?.get("district")}")
                .collection("meetings")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "retrieveMeetings --> $error")
                        return@addSnapshotListener
                    }
                    if (value?.documents?.isNotEmpty() == true) {
                        value.documents[0]?.toObject<Meeting?>().also {
                            meeting.postValue(it)
                        }
                    }
                }
        } catch (e: FirebaseException) {
            Log.e(TAG, "saveMeetingInfo --> $e")
        }
    }


    fun saveMeetingInfo(meeting: Meeting) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = auth.currentUser?.email
            val admin = async {
                email?.let { UserOperations.getAdmin(it) }
            }
            siteRef.document("siteName:${admin.await()?.get("siteName")}" +
                    "-city:${admin.await()?.get("city")}" +
                    "-district:${admin.await()?.get("district")}")
                .collection("meetings")
                .document(meeting.id)
                .set(meeting).await()
        } catch (e: FirebaseException) {
            Log.e(TAG, "saveMeetingInfo --> $e")
        }
    }

    fun addMeetingUriIntoDB(id: String, uri: String) = CoroutineScope(ioDispatcher).launch {
        try {
            val email = auth.currentUser?.email
            val admin = async {
                email?.let { UserOperations.getAdmin(it) }
            }
            siteRef.document("siteName:${admin.await()?.get("siteName")}" +
                    "-city:${admin.await()?.get("city")}" +
                    "-district:${admin.await()?.get("district")}")
                .collection("meetings")
                .document(id)
                .update("meetingUri", uri).await()
        } catch (e: FirebaseException) {
            Log.e(TAG, "saveMeetingInfo --> $e")
        }
    }

    fun retrieveBlockNamesBasedOnSiteInfo(
        siteName: String,
        city: String,
        district: String,
        blockNamesList: MutableLiveData<List<String?>>,
        totalFlatCount: MutableLiveData<Long?>
    ) = CoroutineScope(ioDispatcher).launch {
        siteRef.whereEqualTo("siteName", siteName)
            .whereEqualTo("city", city)
            .whereEqualTo("district", district)
            .get()
            .await()
            .also {
                val documents = it.documents
                val retrievedBlockNames = mutableListOf<String?>()
                var flatCount = 0L
                documents.forEach { document ->
                    if (document["blockName"] != null) {
                        retrievedBlockNames.add(document["blockName"].toString())
                    }
                    flatCount = document["flatCount"].toString().toLong()
                }.also {
                    blockNamesList.postValue(retrievedBlockNames)
                    totalFlatCount.postValue(flatCount)
                }
            }
    }

    fun retrieveAllSitesBasedOnCityAndDistrict(city: String, district: String, allSites: MutableLiveData<MutableList<Site?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            siteRef.whereEqualTo("city", city)
                .whereEqualTo("district", district)
                .get()
                .await().also {
                    val documents = it.documents
                    val siteList = mutableListOf<Site?>()
                    documents.forEach { document ->
                        document?.let { snapshot ->
                            siteList.add(
                                Site(
                                    snapshot["siteName"].toString(),
                                    snapshot["city"].toString(),
                                    snapshot["district"].toString(),
                                    snapshot["blockCount"].toString(),
                                    snapshot["flatCount"].toString().toLong(),
                                    monthlyPayment = snapshot["monthlyPayment"].toString().toLong(),
                                )
                            )
                        }
                        println(document["siteName"])
                    }.also {
                        allSites.postValue(siteList)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveAllSitesBasedOnCityAndDistrict --> $e")
        }
    }

    fun saveExpenditure(expenditure: Expenditure) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val admin = async {
                    UserOperations.getAdmin(requireNotNull(currentUserEmail))
                }
                val setTask = siteRef
                    .document("siteName:${admin.await()?.get("siteName")}" +
                            "-city:${admin.await()?.get("city")}-district:${admin.await()?.get("district")}")
                    .collection("expenditures")
                    .document(expenditure.id)
                    .set(expenditure)
                setTask.continueWith {
                    launch {
                        siteRef
                            .document("siteName:${admin.await()?.get("siteName")}" +
                                    "-city:${admin.await()?.get("city")}-district:${admin.await()?.get("district")}")
                            .update("expendituresAmount", FieldValue.increment(expenditure.amount))
                    }
                }.await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveExpenditure --> $e")
            }
        }
    }
}