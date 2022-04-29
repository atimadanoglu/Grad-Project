package com.graduationproject.grad_project.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.model.ResidentsWhoVoted
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
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.email?.let {
                    val admin = UserOperations.getAdmin(it)
                    siteRef
                        .document("siteName:${admin?.get("siteName")}" +
                                "-city:${admin?.get("city")}" +
                                "-district:${admin?.get("district")}")
                        .collection("voting")
                        .document(voting.id)
                        .set(voting)
                        .await()
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveVotingIntoDBForAdmin --> $e")
            }
        }
    }

    fun acceptButtonClicked(voting: Voting) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val resident = UserOperations.getResident(it)
                siteRef
                    .document("siteName:${resident?.get("siteName")}" +
                            "-city:${resident?.get("city")}" +
                            "-district:${resident?.get("district")}")
                    .collection("voting")
                    .document(voting.id)
                    .update("totalYes", FieldValue.increment(1))
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "acceptButtonClicked --> $e")
        }
    }

    fun rejectButtonClicked(voting: Voting) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val resident = UserOperations.getResident(it)
                siteRef
                    .document("siteName:${resident?.get("siteName")}" +
                            "-city:${resident?.get("city")}" +
                            "-district:${resident?.get("district")}")
                    .collection("voting")
                    .document(voting.id)
                    .update("totalNo", FieldValue.increment(1))
                    .await()
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "rejectButtonClicked --> $e")
        }
    }

    fun saveResidentWhoVoted(voting: Voting, votingResult: Boolean) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val resident = UserOperations.getResident(it)
                val newList = voting.residentsWhoVoted
                newList.add(ResidentsWhoVoted(resident?.get("email").toString(), votingResult))
                siteRef
                    .document("siteName:${resident?.get("siteName")}" +
                            "-city:${resident?.get("city")}" +
                            "-district:${resident?.get("district")}")
                    .collection("voting")
                    .document(voting.id)
                    .update("residentsWhoVoted", newList)
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveResidentWhoVoted --> $e")
        }
    }

    fun retrieveFinishedVotingForResident(votingList: MutableLiveData<MutableList<Voting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val resident = UserOperations.getResident(it)
                siteRef.document("siteName:${resident?.get("siteName")}" +
                        "-city:${resident?.get("city")}" +
                        "-district:${resident?.get("district")}")
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
                            votingList.postValue(retrievedList)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveFinishedVoting --> $e")
        }
    }

    fun retrieveFinishedVotingForAdmin(votingList: MutableLiveData<MutableList<Voting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val admin = UserOperations.getAdmin(it)
                siteRef.document("siteName:${admin?.get("siteName")}" +
                        "-city:${admin?.get("city")}" +
                        "-district:${admin?.get("district")}")
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
                            votingList.postValue(retrievedList)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveFinishedVoting --> $e")
        }
    }

    private fun isFinished(votingDate: Long): Boolean {
        val date = Date().time
        return votingDate - date < 0
    }

    fun retrieveContinuesVotingForResident(votingList: MutableLiveData<MutableList<Voting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val resident = UserOperations.getResident(it)
                siteRef.document("siteName:${resident?.get("siteName")}" +
                        "-city:${resident?.get("city")}" +
                        "-district:${resident?.get("district")}")
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
                            votingList.postValue(retrievedList)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveContinuesVoting --> $e")
        }
    }

    fun retrieveContinuesVotingForAdmin(votingList: MutableLiveData<MutableList<Voting?>>) = CoroutineScope(ioDispatcher).launch {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.email?.let {
                val admin = UserOperations.getAdmin(it)
                siteRef.document("siteName:${admin?.get("siteName")}" +
                        "-city:${admin?.get("city")}" +
                        "-district:${admin?.get("district")}")
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
                            votingList.postValue(retrievedList)
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveContinuesVoting --> $e")
        }
    }

}