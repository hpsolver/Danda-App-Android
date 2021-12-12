package com.sammyekaran.danda.repositry

import com.sammyekaran.danda.model.ForgotPasswordModel
import com.sammyekaran.danda.model.LoginModel
import com.sammyekaran.danda.model.blockuserlist.BlockUserResponse
import com.sammyekaran.danda.model.checkout.CheckOutResponse
import com.sammyekaran.danda.model.exploreDataResponse.ExploreResponse
import com.sammyekaran.danda.model.fcmnotification.NotificationRequest
import com.sammyekaran.danda.model.fetchprofile.FetchProfileModel
import com.sammyekaran.danda.model.fetchsupportchat.FetchChatResponse
import com.sammyekaran.danda.model.fetchticket.FetchTicketsResponse
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.followlist.FollowListModel
import com.sammyekaran.danda.model.getcomment.GetCommentModel
import com.sammyekaran.danda.model.homefeed.HomeFeedModel
import com.sammyekaran.danda.model.isStripeAccount.IsStripeAccount
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.getWatermarkUrl.WatermarkVideoModel
import com.sammyekaran.danda.model.likedislike.LikeDislikeModel
import com.sammyekaran.danda.model.live.LiveModel
import com.sammyekaran.danda.model.notification.NotificationModel
import com.sammyekaran.danda.model.postcomment.PostCommentModel
import com.sammyekaran.danda.model.postdetail.PostDetailModel
import com.sammyekaran.danda.model.profile.ProfileModel
import com.sammyekaran.danda.model.register.RegisterResponse
import com.sammyekaran.danda.model.reportReason.ReportReasonesponse
import com.sammyekaran.danda.model.resetResponse.ResetPasswordResponse
import com.sammyekaran.danda.model.saveStripeId.SaveStripeId
import com.sammyekaran.danda.model.searchuser.SearchUserModel
import com.sammyekaran.danda.model.sharePost.SharePostResponse
import com.sammyekaran.danda.model.splitPayment.SplitPaymentResponse
import com.sammyekaran.danda.model.subscription.SubscriptionResponse
import com.sammyekaran.danda.model.supportchat.SupportChatResponse
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.model.trending.TrendingResponse
import com.sammyekaran.danda.model.updateProfilePic.UpdateProfilePicResponse
import com.sammyekaran.danda.model.updateprofile.UpdateProfileModel
import com.sammyekaran.danda.model.uploadPost.UploadPostModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //==================================LOGIN=========================================
    @FormUrlEncoded
    @POST("api.php?action=login")
    fun login(
            @Field("email") email: String,
            @Field("device_token") deciceToken: String,
            @Field("fcm_token") fcmToken: String,
            @Field("device_type") deviceType: String,
            @Field("password") password: String,
            @Field("deviceId") deviceId: String,
            @Field("type") loginType: String
    ): Call<LoginModel>


    //===================================FORGOT_PASSWORD===================================
    @FormUrlEncoded
    @POST("api.php?action=forgot_password")
    fun forgotPassword(@Field("email") email: String): Call<ForgotPasswordModel>

    //===================================REGISTER============================================
    @FormUrlEncoded
    @POST("api.php?action=register")
    fun register(
            @Field("fullname") name: String,
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("contact") contact: String,
            @Field("device_type") deviceType: String,
            @Field("device_token") deviceToken: String,
            @Field("fcm_token") fcmToken: String,
            @Field("country_code") countryCode: String,
            @Field("countryIso") countryIso: String,
            @Field("deviceId") deviceId: String

    ): Call<RegisterResponse>

    //==========================================GET_PROFILE==================================

    @GET("api.php?action=get_profile")
    fun getProfile(
            @Query("user_id") userId: String,
            @Query("from_userid") fromUserId: String
    ): Call<ProfileModel>

    //==========================================FETCH_PROFILE==================================

    @FormUrlEncoded
    @POST("api.php?action=fetch_profile")
    fun fetchProfile(@Field("user_id") userId: String): Call<FetchProfileModel>


    //===========================================GET_NOTIFICATION=============================
    @FormUrlEncoded
    @POST("api.php?action=get_noti")
    fun getNotification(
            @Field("user_id") userId: String,
            @Field("pageNo") pageNo: String
    ): Call<NotificationModel>

    //===========================================SEARCH_USER=============================
    @FormUrlEncoded
    @POST("api.php?action=search_user")
    fun searchUser(
            @Field("user_id") userId: String,
            @Field("search") search: String,
            @Field("pageNo") pageNo: String
    ): Call<SearchUserModel>

    //=======================================HOME_FEED=====================================================

    @GET("api.php?action=fetch_feed")
    fun fetchFeed(
            @Query("user_id") userId: String,
            @Query("pageNo") pageNo: String,
            @Query("device_type") deviceType: String,
            @Query("device_token") deviceToken: String,
            @Query("deviceId") deviceId: String,
            @Query("fcm_token") fcmToken: String

    ): Call<HomeFeedModel>

    //=======================================POST_DETAIL====================================================
    @FormUrlEncoded
    @POST("api.php?action=detail_page")
    fun detailPage(@Field("post_id") postId: String, @Field("user_id") userid: String): Call<PostDetailModel>

    //=======================================FOLLOW_AND_UNFOLLOW====================================================
    @FormUrlEncoded
    @POST("api.php?action=follow_request")
    fun followUnfollow(
            @Field("user_id") userId: String,
            @Field("follower_id") followerId: String,
            @Field("follower") follower: String
    ): Call<FollowUnfollowModel>

    //==========================================UPDATE_PROFILE==================================
    @Multipart
    @POST("api.php?action=update_profile")
    fun updateProfile(
            @PartMap data: HashMap<String, RequestBody>,
            @Part profilePic: MultipartBody.Part
    ): Call<UpdateProfileModel>

    //===============================================FOLLOW_LIST==================================
    @FormUrlEncoded
    @POST("api.php?action=follow_list")
    fun followList(
            @Field("id") id: String,
            @Field("search") search: String,
            @Field("user_type") type: String,
            @Field("pageNo") pageNo: String,
            @Field("user_id") userId: String
    ): Call<FollowListModel>


    //===============================================GET_COMMENT==================================

    @FormUrlEncoded
    @POST("api.php?action=get_comments")
    fun fetchComment(@Field("post_id") postId: String): Call<GetCommentModel>

    //====================================POST_COMMENT===========================

    @FormUrlEncoded
    @POST("api.php?action=post_comments")
    fun postComment(
            @Field("user_id") userId: String,
            @Field("post_id") postId: String,
            @Field("comment") comment: String,
            @Field("tagUsers") tagUser: String
    ): Call<PostCommentModel>

    //====================================UPLOAD_FEED==================================
    @Multipart
    @POST("api.php?action=post_upload")
    fun uploadFeed(
            @PartMap mParms: HashMap<String, RequestBody>,
            @Part post: MultipartBody.Part
    ): Call<UploadPostModel>


    //====================================UPDATE_PROFILE_PIC==================================
    @Multipart
    @POST("api.php?action=update_profile_pic")
    fun updateProfilePic(
            @PartMap mParms: HashMap<String, RequestBody>,
            @Part post: MultipartBody.Part
    ): Call<UpdateProfilePicResponse>


    //====================================POST_LIKE==================================
    @FormUrlEncoded
    @POST("api.php?action=post_likes")
    fun postLikes(
            @Field("user_id") userId: String,
            @Field("post_id") postId: String,
            @Field("is_like") isLike: String
    ): Call<LikeDislikeModel>

    //====================================ADD_FRIEND==================================
    @FormUrlEncoded
    @POST("api.php?action=add_friendlist")
    fun addFriend(
            @Field("to_userid") toUserId: String,
            @Field("from_userid") fromUserId: String
    ): Call<CommonModel>


    //====================================CHANGE_PASSWORD==============================
    @FormUrlEncoded
    @POST("api.php?action=changePass")
    fun changePassword(
            @Field("user_id") userId: String,
            @Field("oldpassword") oldPassword: String,
            @Field("newpassword") newPassword: String
    ): Call<CommonModel>


    //====================================SEND_NOTIFICATION==============================
    @POST("fcm/send")
    fun fcmNotification(
            @Header("Authorization") token: String,
            @Body request: NotificationRequest
    ): Call<CommonModel>

    //====================================BLOCK_UNBLOCK==============================
    @FormUrlEncoded
    @POST("api.php?action=block_user")
    fun blockUnblock(
            @Field("to_userid") toUser: String,
            @Field("from_userid") fromUserId: String,
            @Field("is_block") isBlock: String
    ): Call<CommonModel>


    //====================================GENRATE_TICKET==============================
    @FormUrlEncoded
    @POST("api.php?action=genrateTicket")
    fun genrateTicket(
            @Field("user_id") userId: String,
            @Field("query") query: String
    ): Call<CommonModel>

    //====================================FETCH_TICKET==============================
    @FormUrlEncoded
    @POST("api.php?action=fetchTicket")
    fun fetchTicket(@Field("user_id") userId: String): Call<FetchTicketsResponse>


    //====================================SEND_MSG_TO_SUPPORT==============================
    @FormUrlEncoded
    @POST("api.php?action=UserChat")
    fun sendMsgToSupport(
            @Field("user_id") userId: String,
            @Field("query_id") queryId: String,
            @Field("query") query: String
    ): Call<SupportChatResponse>

    //====================================FETCH_USER_CHAT==============================
    @FormUrlEncoded
    @POST("api.php?action=fetchUserChat")
    fun fetchUserChat(@Field("query_id") queryId: String): Call<FetchChatResponse>


    //====================================GET_REPORT_REASONS==============================
    @GET("api.php?action=get_reportReasons")
    fun getReportReasons(): Call<ReportReasonesponse>

    //====================================GET_REPORT_REASONS==============================
    @FormUrlEncoded
    @POST("api.php?action=add_reportReasons")
    fun addReportReasons(
            @Field("reason_id") reasonId: String,
            @Field("to_userid") toUserid: String,
            @Field("post_id") postId: String,
            @Field("from_userid") fromUserId: String
    ): Call<ReportReasonesponse>


    //====================================GET_REPORT_REASONS==============================
    @FormUrlEncoded
    @POST("api.php?action=block_list")
    fun getBlockList(@Field("user_id") userId: String): Call<BlockUserResponse>

    //====================================GET_REPORT_REASONS==============================
    @FormUrlEncoded
    @POST("api.php?action=del_post")
    fun deletePost(@Field("post_id") postId: String): Call<CommonModel>

    //====================================CHECK_CONTACT==============================
    @FormUrlEncoded
    @POST("api.php?action=check_contact")
    fun userIsExist(@Field("email") email: String,
                    @Field("contact") contact: String): Call<CommonModel>

    //====================================POST_VIEW==============================
    @FormUrlEncoded
    @POST("api.php?action=count_views")
    fun postView(@Field("user_id") userId: String,
                 @Field("post_id") contact: String): Call<CommonModel>

    //====================================LOGOUT====================================================
    @FormUrlEncoded
    @POST("api.php?action=logout")
    fun doLogout(@Field("user_id") userId: String,
                 @Field("deviceId") deviceId: String): Call<CommonModel>

    //================================Explore Data==================================================
    @FormUrlEncoded
    @POST("api.php?action=exploreData")
    fun getExploreData(@Field("user_id") userId: String, @Field("pageNo") pageNo: String): Call<ExploreResponse>

    //================================Reset Password================================================
    @FormUrlEncoded
    @POST("api.php?action=forgetPasswordByContact")
    fun resetPassword(@Field("contact") phone: String, @Field("password") password: String): Call<ResetPasswordResponse>

    //================================Tag Suggestion================================================
    @FormUrlEncoded
    @POST("api.php?action=tagSuggestions")
    fun tagSuggestion(@Field("name") name: String): Call<TagSuggestion>

    //====================================GET WATERMARK VIDEO URL===================================
    @FormUrlEncoded
    @POST("api.php?action=watermark_video")
    fun getWaterMarkVideoUrl(
            @Field("vdo_name") video_name: String): Call<WatermarkVideoModel>

    //====================================TRENDING==================================================
    @GET("api.php?action=trending")
    fun getTrending(): Call<TrendingResponse>

    //====================================VIEW ALL TRENDING=========================================
    @FormUrlEncoded
    @POST("api.php?action=viewAllTrendings")
    fun getAllTrending(@Field("pageNo") pageNo: String,
                       @Field("type") type: String): Call<HomeFeedModel>

    //===========================================SHARE POST=========================================
    @FormUrlEncoded
    @POST("api.php?action=sharePost")
    fun sharePost(@Field("user_id") userId: String,
                  @Field("post_id") postId: String,
                  @Field("caption") caption: String): Call<SharePostResponse>

}