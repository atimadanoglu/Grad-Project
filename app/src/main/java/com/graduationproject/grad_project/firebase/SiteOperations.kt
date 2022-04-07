package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.FirebaseException
import com.graduationproject.grad_project.components.SnackBars
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
}