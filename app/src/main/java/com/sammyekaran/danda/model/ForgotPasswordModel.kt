package com.sammyekaran.danda.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForgotPasswordModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null


    inner class Response {

        @SerializedName("status")
        @Expose
        var status: String= ""
        @SerializedName("message")
        @Expose
        var message: String =""

    }

}