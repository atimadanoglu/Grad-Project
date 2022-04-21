package com.graduationproject.grad_project.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers

open class FirebaseConstants {

    companion object {
        private const val TAG = "FirebaseConstants"
    }

    protected val ioDispatcher: CoroutineDispatcher by lazy {
        Dispatchers.IO
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

    protected val currentUser: FirebaseUser? by lazy {
        auth.currentUser
    }

    protected val storageRef: StorageReference by lazy {
        storage.reference
    }

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}