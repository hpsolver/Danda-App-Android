package com.sammyekaran.danda.model.homefeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("result")
    @Expose
    var result: MutableList<Result>? = null
    @SerializedName("suggestions")
    @Expose
    var suggestions: MutableList<Suggestion>? = null
    @SerializedName("is_donate")
    @Expose
    var isDonate:String? = null
    @SerializedName("is_membership")
    @Expose
    var isMembership:String? = null
    @SerializedName("noti_count")
    @Expose
    var notiCount:String? = null
    @SerializedName("subscriptionType")
    @Expose
    var subscriptionType:String? = null

}