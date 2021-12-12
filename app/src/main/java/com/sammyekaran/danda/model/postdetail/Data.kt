package com.sammyekaran.danda.model.postdetail

import com.sammyekaran.danda.model.homefeed.Link
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {

    @SerializedName("user_id")
    @Expose
    var userId: String=""
    @SerializedName("username")
    @Expose
    var username: String=""
    @SerializedName("profile_pic")
    @Expose
    var profilePic: String=""
    @SerializedName("posts")
    @Expose
    var posts: String=""
    @SerializedName("thumbnail_url")
    @Expose
    var thumbnailUrl: String=""
    @SerializedName("posts_type")
    @Expose
    var postsType: String=""
    @SerializedName("desc")
    @Expose
    var desc: List<Link>? = null
    @SerializedName("post_id")
    @Expose
    var postId: String=""
    @SerializedName("caption")
    @Expose
    var caption: String=""
    @SerializedName("is_explicit")
    @Expose
    var isExplicit: String=""
    @SerializedName("likes_count")
    @Expose
    var likesCount: String=""
    @SerializedName("likes")
    @Expose
    var likes: String=""
    @SerializedName("comments_count")
    @Expose
    var commentsCount: String=""
    @SerializedName("comments")
    @Expose
    var comments: List<Comment>? = null
    @SerializedName("copy_url")
    @Expose
    var copyUrl: String? = null
    @SerializedName("is_view")
    @Expose
    var isView: String? = null
    @SerializedName("total_views")
    @Expose
    var totalViews: String? = null
    @SerializedName("follower_profile")
    @Expose
    var followerprofile: String? = null

}