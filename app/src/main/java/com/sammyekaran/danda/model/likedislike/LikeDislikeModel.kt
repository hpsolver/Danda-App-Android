package com.sammyekaran.danda.model.likedislike

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikeDislikeModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}