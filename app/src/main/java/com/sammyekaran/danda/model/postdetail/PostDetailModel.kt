package com.sammyekaran.danda.model.postdetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostDetailModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}