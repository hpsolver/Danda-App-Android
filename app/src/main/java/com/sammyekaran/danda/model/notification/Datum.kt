package com.sammyekaran.danda.model.notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum {

    @SerializedName("post_id")
    @Expose
    var postId: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("notification")
    @Expose
    var notification: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("caption")
    @Expose
    var caption: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null

}