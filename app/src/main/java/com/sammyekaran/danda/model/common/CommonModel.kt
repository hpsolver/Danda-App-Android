package com.sammyekaran.danda.model.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sammyekaran.danda.model.likedislike.Response

class CommonModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}