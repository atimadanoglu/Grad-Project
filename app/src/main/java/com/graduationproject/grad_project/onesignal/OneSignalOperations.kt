package com.graduationproject.grad_project.onesignal

import android.util.Log
import com.graduationproject.grad_project.firebase.UserOperations
import com.graduationproject.grad_project.model.Notification
import com.onesignal.OneSignal
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object OneSignalOperations {

    fun postNotification(
        playerIDs: ArrayList<String>,
        notification: Notification
    ) {
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

    /**
     * It will be used to send push notification to residents by using
     * their player_ids
     * @param adminEmail It's for taking the administrator's info from db
     * * */
    suspend fun takePlayerIDs(adminEmail: String): ArrayList<String> {
        val admin = UserOperations.getAdmin(adminEmail)

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