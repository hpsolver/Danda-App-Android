package com.sammyekaran.danda.model.fetchprofile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FetchProfileModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}