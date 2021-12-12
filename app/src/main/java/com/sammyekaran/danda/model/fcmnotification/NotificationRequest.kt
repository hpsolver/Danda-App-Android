package com.sammyekaran.danda.model.fcmnotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationRequest {

    @SerializedName("to")
    @Expose
    var to: String? = null
    @SerializedName("collapse_key")
    @Expose
    var collapseKey: String? = null
    @SerializedName("notification")
    @Expose
    var notification: Notification? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

}