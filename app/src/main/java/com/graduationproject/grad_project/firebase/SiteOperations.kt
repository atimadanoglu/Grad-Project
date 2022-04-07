package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.FirebaseException
import com.graduationproject.grad_project.components.SnackBars
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object SiteOperations: FirebaseConstants() {

    private const val TAG = "SiteOperations"

    suspend fun saveSiteInfoIntoDB(
        site: HashMap<String, Any>,
        scope: CoroutineDispatcher = Dispatchers.IO
    ): Boolean {
        return withContext(scope + coroutineExceptionHandler) {
            try {
                siteRef
                    .document("siteName:${site["siteName"]}-city:${site["city"]}-district:${site["district"]}")
                    .set(site).await()
                true
            } catch (e: FirebaseException) {
                Log.e(TAG, "saveSiteInfoDB --> $e")
                false
            }
        }

    }
}