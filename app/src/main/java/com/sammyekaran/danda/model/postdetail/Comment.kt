package com.sammyekaran.danda.model.postdetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Comment {

    @SerializedName("comment")
    @Expose
    var comment: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null

}