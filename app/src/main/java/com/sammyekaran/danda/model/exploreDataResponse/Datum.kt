package com.sammyekaran.danda.model.exploreDataResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("upload_id")
    @Expose
    var uploadId: String? = null
    @SerializedName("upload")
    @Expose
    var upload: String? = null
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null
    @SerializedName("upload_type")
    @Expose
    var uploadType: String? = null
    @SerializedName("is_explicit")
    @Expose
    var isExplicit: String? = null
    @SerializedName("isFollowSubscriptionPurchased")
    @Expose
    var isFollowSubscriptionPurchased: String? = null

}