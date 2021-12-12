package com.sammyekaran.danda.model.searchuser

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum {

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
    @SerializedName("user_type")
    @Expose
    var userType: String? = null

}