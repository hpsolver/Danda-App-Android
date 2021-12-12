package com.sammyekaran.danda.model.homefeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Shared {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("caption")
    @Expose
    var caption: String? = null

    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
}