package com.graduationproject.grad_project.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.graduationproject.grad_project.FirebaseService
import com.graduationproject.grad_project.RetrofitInstance
import com.graduationproject.grad_project.model.Notification
import com.graduationproject.grad_project.model.PushNotification
import com.graduationproject.grad_project.view.admin.AddAnnouncementFragment
import kotlinx.coroutines.*

class AddAnnouncementViewModel: ViewModel() {

    companion object {
        private const val TAG = "AddAnnouncementViewModel"
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                val json = Gson().toJson(response.headers())
                Log.d(TAG, "Response: $json")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "hata burada $e")
        }
    }

    fun registerTheDeviceAndTopicAndSendItToUsers(
        context: Context,
        tag: String,
        getTopicName: String?,
        getAnnouncementInfo: Notification
    ) {
        FirebaseService.sharedPreferences = context.getSharedPreferences("getSharedPreferences",
            Context.MODE_PRIVATE
        )
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(tag, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            FirebaseService.token = token
            Toast.makeText(context, token, Toast.LENGTH_SHORT).show()
        })
        getTopicName?.let { FirebaseMessaging.getInstance().subscribeToTopic(it) }
        getTopicName?.let {
            FirebaseService.token?.let { token ->
                PushNotification(
                    getAnnouncementInfo,
                    token
                ).also { pushNotification ->
                    sendNotification(pushNotification)
                }
            }
        }
    }



}