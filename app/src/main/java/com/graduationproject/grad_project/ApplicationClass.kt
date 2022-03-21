package com.graduationproject.grad_project

import android.app.Application
import com.onesignal.OneSignal

const val ONESIGNAL_APP_ID = "50eab86c-78a0-4258-9bda-b8f731d3ab33"

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}