package com.sammyekaran.danda.model.checkout

import com.google.gson.annotations.SerializedName

class Response {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: String? = null

}