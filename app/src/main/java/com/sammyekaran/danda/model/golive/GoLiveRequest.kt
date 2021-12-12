package com.sammyekaran.danda.model.golive

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GoLiveRequest {

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("broadcast_id")
    @Expose
    var broadcastId: String? = null

}