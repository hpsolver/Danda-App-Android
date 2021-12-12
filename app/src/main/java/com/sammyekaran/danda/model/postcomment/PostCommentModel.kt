package com.sammyekaran.danda.model.postcomment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostCommentModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}