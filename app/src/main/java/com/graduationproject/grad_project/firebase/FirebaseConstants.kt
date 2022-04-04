package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduationproject.grad_project.view.admin.AddAnnouncementFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.tasks.await

open class FirebaseConstants {

    companion object {
        private const val TAG = "FirebaseConstants"
    }

    protected val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    protected val adminRef: CollectionReference by lazy {
        db.collection("administrators")
    }
    protected val residentRef: CollectionReference by lazy {
        db.collection("residents")
    }
    protected val siteRef: CollectionReference by lazy {
        db.collection("sites")
    }
    protected val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    protected val storageRef: StorageReference by lazy {
        storage.reference
    }

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}