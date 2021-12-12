package com.sammyekaran.danda.model.fcmnotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Notification {

    @SerializedName("body")
    @Expose
    var body: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null

}