package com.sammyekaran.danda.model.uploadPost

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadPostModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}