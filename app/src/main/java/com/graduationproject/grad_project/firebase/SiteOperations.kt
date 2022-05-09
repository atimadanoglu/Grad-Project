package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.model.Expenditure
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