package com.graduationproject.grad_project.firebase

import android.net.Uri
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.tasks.await
import java.util.*

object StorageOperations: FirebaseConstants() {

    private const val TAG = "StorageOperations"

    suspend fun uploadImage(selectedPicture: Uri?): Uri? {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val imageReference = storageRef.child("announcementDocuments").child(imageName)
        if (selectedPicture != null) {
            return try {
                imageReference.putFile(selectedPicture).addOnSuccessListener {
                    Log.d(TAG, "Images successfully uploaded!")
                }.addOnFailureListener {
                    Log.e(TAG, it.toString())
                }.await()
                val a = imageReference.downloadUrl.await()
                a
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                null
            }
        }
        return null
    }
}