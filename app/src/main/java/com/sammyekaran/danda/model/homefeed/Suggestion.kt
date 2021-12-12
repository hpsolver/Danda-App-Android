package com.sammyekaran.danda.model.homefeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Suggestion {

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("fullname")
    @Expose
    var fullname: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("users_type")
    @Expose
    var userType: String? = null

}