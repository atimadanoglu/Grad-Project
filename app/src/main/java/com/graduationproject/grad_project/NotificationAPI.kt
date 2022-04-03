package com.graduationproject.grad_project

import com.graduationproject.grad_project.FCMConstants.Companion.CONTENT_TYPE
import com.graduationproject.grad_project.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization:key=${BuildConfig.API_KEY}", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody >
}