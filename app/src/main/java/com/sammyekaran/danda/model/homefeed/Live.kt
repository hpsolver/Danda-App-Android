package com.sammyekaran.danda.model.homefeed

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
@Keep class Live {

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("broadcast_id")
    @Expose
    var broadcastId: String? = null
    @SerializedName("broadcast_url")
    @Expose
    var broadcastUrl: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null

}