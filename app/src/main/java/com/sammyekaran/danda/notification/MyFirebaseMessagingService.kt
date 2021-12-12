package com.sammyekaran.danda.notification

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    var notification: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    companion object {
        private val TAG = "MyFirebaseMessagingServ"
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Log.d("PAY_LOAD", remoteMessage?.data.toString())

        if (remoteMessage!!.data != null) {
            try {
                val jsonObject = JSONObject(remoteMessage.data as Map<*, *>)
                val notiType = jsonObject.getString("noti_type")
                if (notiType.equals("post comment")){
                    broadcastNotiCount( jsonObject.getString("noti_count"))
                    NotificationHelper.showNotification(
                        applicationContext,
                        "Danda",
                        jsonObject.getString("message"),
                        notiType,
                        jsonObject.getString("profile_pic"),
                        jsonObject.getString("post_id")
                    )
                }else if (notiType.equals("firebaseMsg")){
                    NotificationHelper.showMsgNotification(
                        applicationContext,
                        jsonObject.getString("fromUser"),
                        jsonObject.getString("fromUserName"),
                        jsonObject.getString("fromUserToken"),
                        jsonObject.getString("body")
                    )
                }else if (notiType.equals("post like")){
                    broadcastNotiCount( jsonObject.getString("noti_count"))
                    NotificationHelper.showNotification(
                        applicationContext,
                        "Danda",
                        jsonObject.getString("message"),
                        notiType,
                        jsonObject.getString("profile_pic"),
                        jsonObject.getString("post_id")
                    )
                }else if (notiType.equals("follow_request")){
                    broadcastNotiCount( jsonObject.getString("noti_count"))
                    NotificationHelper.showNotification(
                        applicationContext,
                        "Danda",
                        jsonObject.getString("message"),
                        notiType,
                        jsonObject.getString("profile_pic"),
                        jsonObject.getString("user_id")
                    )
                }
                else if (notiType == "warning" || notiType == "all"){
                    broadcastNotiCount( jsonObject.getString("noti_count"))
                    NotificationHelper.showNotification(
                        applicationContext,
                        "Danda",
                        jsonObject.getString("message"),
                        notiType,
                        jsonObject.getString("profile_pic"),
                        jsonObject.getString("user_id")
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

    }

    private fun broadcastNotiCount(
        count: String
    ) {
        val intent = Intent()
        intent.action = "com.appzorro.noti"
        intent.putExtra("count", count)
        sendBroadcast(intent)
    }


}