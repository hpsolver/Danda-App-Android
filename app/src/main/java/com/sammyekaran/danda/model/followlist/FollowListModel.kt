package com.sammyekaran.danda.model.followlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FollowListModel {

    @SerializedName("response")
    @Expose
    var response: Response? = null

}