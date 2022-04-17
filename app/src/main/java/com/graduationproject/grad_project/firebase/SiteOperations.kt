package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.components.SnackBars
import com.graduationproject.grad_project.model.Expenditure
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

    fun saveExpenditure(email: String, expenditure: Expenditure) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val admin = async {
                    UserOperations.getAdmin(email)
                }
                launch {
                    siteRef
                        .document("siteName:${admin.await()?.get("siteName")}" +
                                "-city:${admin.await()?.get("city")}-district:${admin.await()?.get("district")}")
                        .collection("expenditures")
                        .document(expenditure.id)
                        .set(expenditure)
                        .await()
                }
                launch {
                    siteRef
                        .document("siteName:${admin.await()?.get("siteName")}" +
                                "-city:${admin.await()?.get("city")}-district:${admin.await()?.get("district")}")
                        .update("expendituresAmount", FieldValue.increment(expenditure.amount.toLong()))
                        .await()
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveExpenditure --> $e")
            }
        }
    }
}