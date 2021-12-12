package com.sammyekaran.danda.model.followlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("followers")
    @Expose
    var followers: String? = null
    @SerializedName("following")
    @Expose
    var following: String? = null
    @SerializedName("detail")
    @Expose
    var detail: List<Detail>? = null

}