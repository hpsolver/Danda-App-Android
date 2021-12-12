package com.sammyekaran.danda.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Post {

        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("upload_type")
        @Expose
        var uploadType: String? = null
        @SerializedName("explicit")
        @Expose
        var explicit: String? = null
        @SerializedName("post_url")
        @Expose
        var postUrl: String? = null
        @SerializedName("thumbnail_url")
        @Expose
        var thumbnailUrl: String? = null

    }