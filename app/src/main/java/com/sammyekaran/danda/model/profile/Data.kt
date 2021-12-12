package com.sammyekaran.danda.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("website")
    @Expose
    var website: String? = null
    @SerializedName("bio")
    @Expose
    var bio: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("fcm_token")
    @Expose
    var fcmToken: String? = null
    @SerializedName("posts_count")
    @Expose
    var postsCount: String? = null
    @SerializedName("followers_count")
    @Expose
    var followersCount: String? = null
    @SerializedName("following_count")
    @Expose
    var followingCount: String? = null
    @SerializedName("users_types")
    @Expose
    var usersTypes: String? = null
    @SerializedName("is_block")
    @Expose
    var isBlock: String? = null
    @SerializedName("posts")
    @Expose
    var posts: List<Post>? = null
    @SerializedName("copy_url")
    @Expose
    var copyUrl: String? = null
    @SerializedName("from_fullname")
    @Expose
    var fromFullname: String? = null
    @SerializedName("isFollowSubscriptionPurchased")
    @Expose
    var isFollowSubscriptionPurchased: String? = null
    @SerializedName("connected_with_stripe")
    @Expose
    var connected_with_stripe: String? = null

}