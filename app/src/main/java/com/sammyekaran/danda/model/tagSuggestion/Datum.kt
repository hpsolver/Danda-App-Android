package com.sammyekaran.danda.model.tagSuggestion

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum {
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("fullname")
    @Expose
    var fullname: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profile_pic: String? = null

}