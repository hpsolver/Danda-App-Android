package com.sammyekaran.danda.model.homefeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null
    @SerializedName("posts")
    @Expose
    var posts: String? = null
    @SerializedName("follower_profile")
    @Expose
    var followerProfile: String? = null
    @SerializedName("thumbnail_url")
    @Expose
    var thumbnailUrl: String = ""
    @SerializedName("posts_type")
    @Expose
    var postsType: String = ""
    @SerializedName("post_id")
    @Expose
    var postId: String? = null
    @SerializedName("is_explicit")
    @Expose
    var isExplicit: String? = null
    @SerializedName("links")
    @Expose
    var links: List<Link>? = null
    @SerializedName("likes_count")
    @Expose
    var likesCount: String? = null
    @SerializedName("likes")
    @Expose
    var likes: String? = null
    @SerializedName("comments_count")
    @Expose
    var commentsCount: String? = null
    @SerializedName("comments")
    @Expose
    var comments: List<String>? = null
    @SerializedName("shared")
    @Expose
    var shared: List<Shared>? = null
    @SerializedName("caption")
    @Expose
    var caption: String? = null
    @SerializedName("copy_url")
    @Expose
    var copyUrl: String? = null
    @SerializedName("is_view")
    @Expose
    var isView: String? = null
    @SerializedName("total_views")
    @Expose
    var totalViews: String? = null
    @SerializedName("postMessage")
    @Expose
    var postMessage: String? = null
    @SerializedName("isFollowSubscriptionPurchased")
    @Expose
    var isFollowSubscriptionPurchased: String? = null

}