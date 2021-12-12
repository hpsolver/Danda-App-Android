package com.sammyekaran.danda.model.exploreDataResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExploreResponse {
    @SerializedName("response")
    @Expose
    var response: Response? = null

}