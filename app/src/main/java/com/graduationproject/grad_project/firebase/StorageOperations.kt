package com.graduationproject.grad_project.firebase

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.graduationproject.grad_project.view.admin.AddAnnouncementFragment
import kotlinx.coroutines.tasks.await
import java.util.*

object StorageOperations: FirebaseConstants() {

    private const val TAG = "StorageOperations"

    suspend fun uploadImage(selectedPicture: Uri?) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"
        val imageReference = storageRef.child("announcementDocuments").child(imageName)

        if (selectedPicture != null) {
            try {
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                    Log.d(TAG, "Images successfully uploaded!")
                }.addOnFailureListener {
                    Log.w(TAG, it.localizedMessage, it)
                }.await()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}