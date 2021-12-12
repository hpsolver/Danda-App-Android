package com.sammyekaran.danda.model.fcmnotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("body")
    @Expose
    var body: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("noti_type")
    @Expose
    var notiType: String? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("sound")
    @Expose
    var sound: String? = null
    @SerializedName("fromUser")
    @Expose
    var fromUser: String? = null
    @SerializedName("fromUserName")
    @Expose
    var fromUserName: String? = null
    @SerializedName("fromUserToken")
    @Expose
    var fromUserToken: String? = null

}