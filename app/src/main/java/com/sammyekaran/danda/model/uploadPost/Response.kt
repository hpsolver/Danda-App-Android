package com.sammyekaran.danda.model.uploadPost

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response {

    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null

}