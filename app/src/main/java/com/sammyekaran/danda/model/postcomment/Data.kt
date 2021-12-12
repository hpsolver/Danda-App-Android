package com.sammyekaran.danda.model.postcomment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("follower_id")
    @Expose
    var followerId: String? = null
    @SerializedName("comment")
    @Expose
    var comment: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null

}