package com.graduationproject.grad_project.viewmodel.dialogs

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.onesignal.OneSignalOperations
import kotlinx.coroutines.*
import java.util.*

class AddRequestViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    companion object {
        const val TAG = "AddRequestViewModel"
    }

    private var _title = ""
    val title get() = _title

    private var _type = ""
    val type get() = _type

    private var _content = ""
    val content get() = _content

    fun setTitle(title: String) { _title = title }
    fun setType(type: String) { _type = type }
    fun setContent(content: String) { _content = content }

    suspend fun shareRequestWithAdmin(
        residentEmail: String? = FirebaseAuth.getInstance().currentUser?.email
    ) {
        CoroutineScope(mainDispatcher).launch {
            val request = createRequest()
            val notification = createNotification(request)
            val resident = async(ioDispatcher) {
                residentEmail?.let { UserOperations.getResident(it) }
            }
            val admin = async(ioDispatcher) {
                resident.await()?.let { UserOperations.getAdminInSpecificSite(it) }
            }
            val list = arrayListOf<String>()
            val k = async {
                admin.await()?.forEach {
                    list.add(it["player_id"].toString())
                }
            }
            k.await().also {
                if (k.isCompleted) {
                    OneSignalOperations.postNotification(list, notification)
                }
            }

            launch {
                RequestsOperations.saveRequestIntoAdminDB(
                    admin.await()?.documents?.get(0)?.get("email").toString(), request
                )
            }
            launch {
                residentEmail?.let { residentEmail ->
                    RequestsOperations.saveRequestIntoResidentDB(residentEmail, request)
                }
            }
        }.join()
    }

    private fun createNotification(request: Request): Notification {
        val uuid = UUID.randomUUID()
        return Notification(
            request.title,
            request.content,
            "",
            uuid.toString(),
            Timestamp.now()
        )
    }

    private suspend fun createRequest(auth: FirebaseAuth = FirebaseAuth.getInstance()): Request {
        return withContext(mainDispatcher) {
            val uuid = UUID.randomUUID()
            val resident = async(ioDispatcher) {
                auth.currentUser?.email?.let { UserOperations.getResident(it) }
            }
            val sentBy = "${resident.await()?.get("fullName")} " +
                    "- Blok: ${resident.await()?.get("blockNo")} " +
                    "- Daire: ${resident.await()?.get("flatNo")}"
            Request(
                uuid.toString(),
                _title,
                _content,
                _type,
                sentBy,
                Timestamp.now()
            )
        }
    }

}