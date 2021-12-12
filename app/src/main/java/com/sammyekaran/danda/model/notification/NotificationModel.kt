package com.sammyekaran.danda.model.notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}