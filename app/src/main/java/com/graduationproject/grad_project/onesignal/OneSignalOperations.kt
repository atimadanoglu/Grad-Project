package com.graduationproject.grad_project.onesignal

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.onesignal.OneSignal
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object OneSignalOperations {

    fun postNotification(
        playerIDs: ArrayList<String>,
        notification: Notification
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pushNotificationJsonOneSignal =
                    createJsonObjectForNotification(notification.title, notification.message, playerIDs)
                OneSignal.postNotification(
                    pushNotificationJsonOneSignal,
                    object : OneSignal.PostNotificationResponseHandler {
                        override fun onSuccess(response: JSONObject) {
                            Log.i("OneSignalExample", "postNotification Success: $response")
                        }

                        override fun onFailure(response: JSONObject) {
                            Log.e("OneSignalExample", "postNotification Failure: $response")
                        }
                    })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * It will be used to send push notification to residents by using
     * their player_ids
     *
     * * */
    suspend fun takePlayerIDs(): ArrayList<String> {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val admin = UserOperations.getAdmin(requireNotNull(email))

        val residents = admin?.let { UserOperations.getResidentsInASpecificSite(it) }
        val residentDocuments = residents?.documents
        val playerIDs = arrayListOf<String>()
        if (residentDocuments != null) {
            for (document in residentDocuments) {
                playerIDs.add(document["player_id"].toString())
            }
        }
        return playerIDs
    }

    /**
     * It will be used to send push notification to residents by using
     * their player_ids
     * @param adminEmail It's for taking the administrator's info from db
     * * */
    suspend fun takeAdminPlayerIDAndEmail(
        adminEmail: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): ArrayList<String> {
        return withContext(ioDispatcher) {
            val admin = async {
                UserOperations.getAdmin(adminEmail)
            }
            val playerIDs = arrayListOf<String>()

            val playerID = admin.await()?.get("player_id").toString()
            playerIDs.add(playerID)
            playerIDs
        }
    }

    private fun createJsonObjectForNotification(title: String, message: String, playerIDs: ArrayList<String>): JSONObject {
        val jsonObject = JSONObject(
            "{'headings': {'en': '$title'}," +
                    "'contents': {'en': '$message'}," +
                    "'include_player_ids': '[]'}"
        )
        val jsonArray = JSONArray()
        for(playerID in playerIDs) {
            jsonArray.put(playerID)
        }
        jsonObject.put("include_player_ids", jsonArray)
        return jsonObject
    }

    fun savePlayerId(admin: HashMap<String, Any>) {
        val deviceState = OneSignal.getDeviceState()
        val userId = deviceState?.userId
        admin["player_id"] = userId.toString()
    }
}