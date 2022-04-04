package com.graduationproject.grad_project.firebase

import android.net.Uri
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.tasks.await
import java.util.*

object StorageOperations: FirebaseConstants() {

    private const val TAG = "StorageOperations"

    suspend fun uploadImage(view: View, selectedPicture: Uri?) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val imageReference = storageRef.child("announcementDocuments").child(imageName)

        if (selectedPicture != null) {
            try {
                imageReference.putFile(selectedPicture).addOnSuccessListener {
                    Log.d(TAG, "Images successfully uploaded!")
                    Snackbar.make(view, "Fotoğraf başarıyla yüklendi", Snackbar.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Log.w(TAG, it.localizedMessage, it)
                    Snackbar.make(view, "Fotoğraf yükleme başarısız!", Snackbar.LENGTH_LONG).show()
                }.await()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}