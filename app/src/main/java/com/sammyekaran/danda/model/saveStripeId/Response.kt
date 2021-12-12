package com.sammyekaran.danda.model.saveStripeId

import com.google.gson.annotations.SerializedName

class Response {
    @SerializedName("data")
    var data: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: String? = null

}