package com.sammyekaran.danda.model.fetchprofile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("fullname")
    @Expose
    var fullname: String? = null
    @SerializedName("website")
    @Expose
    var website: String? = null
    @SerializedName("bio")
    @Expose
    var bio: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("contact")
    @Expose
    var contact: String? = null
    @SerializedName("gender")
    @Expose
    var gender: String? = null
    @SerializedName("paypal_id")
    @Expose
    var paypalId: String? = null
    @SerializedName("country_code")
    @Expose
    var countryCode: String? = null
    @SerializedName("countryIso")
    @Expose
    var countryIso: String? = null
    @SerializedName("password")
    @Expose
    var password: String? = null

}