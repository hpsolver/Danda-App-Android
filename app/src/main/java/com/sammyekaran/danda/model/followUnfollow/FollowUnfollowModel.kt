package com.sammyekaran.danda.model.followUnfollow

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FollowUnfollowModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}