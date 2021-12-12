package com.sammyekaran.danda.model.live

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LiveModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}