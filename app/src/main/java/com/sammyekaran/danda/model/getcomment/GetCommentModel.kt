package com.sammyekaran.danda.model.getcomment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCommentModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}