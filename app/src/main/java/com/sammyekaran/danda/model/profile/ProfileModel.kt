package com.sammyekaran.danda.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null
}