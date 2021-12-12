package com.sammyekaran.danda.model.followUnfollow

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null

}