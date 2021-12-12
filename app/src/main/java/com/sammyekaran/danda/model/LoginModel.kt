package com.sammyekaran.danda.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

    inner class Response {

        @SerializedName("message")
        @Expose
        var message: String = ""
        @SerializedName("status")
        @Expose
        var status: String? = null
        @SerializedName("data")
        @Expose
        var data: Data? = null

    }

    inner class Data {

        @SerializedName("user_id")
        @Expose
        var userId: String? = null

        @SerializedName("fullname")
        @Expose
        var fullname: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("profile_pic")
        @Expose
        var profile_pic: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null


    }
}