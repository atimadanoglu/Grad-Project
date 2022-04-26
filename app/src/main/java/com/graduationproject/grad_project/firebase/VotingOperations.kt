package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.model.Voting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

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

    fun retrieveFinishedVoting(votingList: MutableLiveData<MutableList<Voting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            currentUserEmail?.let {
                adminRef.document(it)
                    .collection("voting")
                    .whereEqualTo("finished", true)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveVoting --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedList = mutableListOf<Voting?>()
                        documents?.forEach { document ->
                            retrievedList.add(
                                document.toObject<Voting>()
                            )
                        }.also {
                            votingList.value = retrievedList
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveVoting --> $e")
        }
    }

    private fun isFinished(votingDate: Long): Boolean {
        val date = Date().time
        return votingDate - date < 0
    }

    fun retrieveContinuesVoting(votingList: MutableLiveData<MutableList<Voting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            currentUserEmail?.let {
                adminRef.document(it)
                    .collection("voting")
                    .whereEqualTo("finished", false)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e(TAG, "retrieveVoting --> $error")
                            return@addSnapshotListener
                        }
                        val documents = value?.documents
                        val retrievedList = mutableListOf<Voting?>()
                        documents?.forEach { document ->
                            if (isFinished(document["date"].toString().toLong())) {
                                document.reference.update("finished", true)
                            }
                            retrievedList.add(
                                document.toObject<Voting>()
                            )
                        }.also {
                            votingList.value = retrievedList
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveVoting --> $e")
        }
    }

}