package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.graduationproject.grad_project.model.Voting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object VotingOperations: FirebaseConstants() {

    const val TAG = "VotingOperation"

    fun saveVotingIntoDB(voting: Voting) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val email = auth.currentUser?.email
                email?.let {
                    adminRef.document(it)
                        .collection("voting")
                        .document(voting.id)
                        .set(voting)
                        .await()
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveVotingIntoDB --> $e")
            }
        }
    }
}