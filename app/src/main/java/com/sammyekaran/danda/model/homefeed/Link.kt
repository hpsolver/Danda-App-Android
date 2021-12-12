package com.sammyekaran.danda.model.homefeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Link {

    @SerializedName("content")
    @Expose
    var content: String? = null
    @SerializedName("content_description")
    @Expose
    var contentDescription: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null

}