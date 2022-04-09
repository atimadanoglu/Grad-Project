package com.graduationproject.grad_project.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.graduationproject.grad_project.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.collections.ArrayList

object MessagesOperations: FirebaseConstants() {

    private const val TAG = "MessagesOperations"

    suspend fun saveMessageIntoDB(email: String, message: Message): Boolean {
        return withContext(ioDispatcher + coroutineExceptionHandler) {
            try {
                residentRef.document(email)
                    .collection("messages")
                    .document(message.id)
                    .set(message)
                    .await()
                true
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveMessageIntoDB ---> $e")
                false
            }
        }
    }

    suspend fun retrieveMessagesAndSortThem(email: String): ArrayList<Message> {
        return withContext(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val messages = arrayListOf<Message>()
                residentRef.document(email)
                    .collection("messages")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await().forEach { document->
                        messages.add(
                            Message(
                                document.get("title") as String,
                                document.get("content") as String,
                                document.get("id") as String,
                                document.get("date") as Timestamp
                            )
                        )
                    }
                messages
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                arrayListOf()
            }
        }
    }

    suspend fun deleteAllMessages(email: String) {
        withContext(ioDispatcher + coroutineExceptionHandler) {
            try {
                residentRef.document(email)
                    .collection("messages")
                    .get()
                    .await()
                    .forEach { document ->
                        document.reference.delete()
                    }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "deleteAllMessages --> $e")
            }
        }
    }

    suspend fun deleteMessageInSpecificPosition(
        email: String,
        messages: ArrayList<Message>,
        position: Int
    ) {
        withContext(ioDispatcher + coroutineExceptionHandler) {
            try {
                residentRef.document(email)
                    .collection("messages")
                    .document(messages[position].id)
                    .delete().await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "deleteMessageInSpecificPosition --> $e")
            }
        }
    }

    suspend fun retrieveMessages(email: String): ArrayList<Message> {
        return withContext(ioDispatcher + coroutineExceptionHandler) {
            try {
                val documents = residentRef.document(email)
                    .collection("messages")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await().documents
                val messages = arrayListOf<Message>()
                documents.forEach {
                    val message = Message(
                        it["title"].toString(), it["content"].toString(), it["id"].toString(), it["date"] as Timestamp
                    )
                    messages.add(message)
                }
                messages
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "retrieveMessages --> $e")
                arrayListOf()
            }
        }
    }


}