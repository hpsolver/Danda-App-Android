package com.sammyekaran.danda.model.homefeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeFeedModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}