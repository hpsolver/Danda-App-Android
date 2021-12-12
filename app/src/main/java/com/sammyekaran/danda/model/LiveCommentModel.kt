package com.sammyekaran.danda.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LiveCommentModel {

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("comments")
    @Expose
    var comments: String? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("broadcast_id")
    @Expose
    var broadcastId: String? = null

}