package com.sammyekaran.danda.repositry

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.fragment.app.FragmentActivity
import com.sammyekaran.danda.model.ForgotPasswordModel
import com.sammyekaran.danda.model.LoginModel
import com.sammyekaran.danda.model.blockuserlist.BlockUserResponse
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.exploreDataResponse.ExploreResponse
import com.sammyekaran.danda.model.fcmnotification.NotificationRequest
import com.sammyekaran.danda.model.fetchprofile.FetchProfileModel
import com.sammyekaran.danda.model.fetchsupportchat.FetchChatResponse
import com.sammyekaran.danda.model.fetchticket.FetchTicketsResponse
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.followlist.FollowListModel
import com.sammyekaran.danda.model.getWatermarkUrl.WatermarkVideoModel
import com.sammyekaran.danda.model.getcomment.GetCommentModel
import com.sammyekaran.danda.model.homefeed.HomeFeedModel
import com.sammyekaran.danda.model.likedislike.LikeDislikeModel
import com.sammyekaran.danda.model.notification.NotificationModel
import com.sammyekaran.danda.model.postcomment.PostCommentModel
import com.sammyekaran.danda.model.postdetail.PostDetailModel
import com.sammyekaran.danda.model.profile.ProfileModel
import com.sammyekaran.danda.model.register.RegisterResponse
import com.sammyekaran.danda.model.reportReason.ReportReasonesponse
import com.sammyekaran.danda.model.resetResponse.ResetPasswordResponse
import com.sammyekaran.danda.model.searchuser.SearchUserModel
import com.sammyekaran.danda.model.sharePost.SharePostResponse
import com.sammyekaran.danda.model.supportchat.SupportChatResponse
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.model.trending.TrendingResponse
import com.sammyekaran.danda.model.updateProfilePic.UpdateProfilePicResponse
import com.sammyekaran.danda.model.updateprofile.UpdateProfileModel
import com.sammyekaran.danda.model.uploadPost.UploadPostModel
import com.sammyekaran.danda.utils.Constants
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class WebServices {

    var mAppContext: Context? = null
    private var retrofit: Retrofit? = null

    private fun getClient(): Retrofit? {

        retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit
    }


    fun login(
            email: String,
            deciceToken: String,
            fcmToken: String,
            deviceType: String,
            password: String,
            deviceId: String,
            loginType: String
    ): Call<LoginModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.login(email, deciceToken, fcmToken, deviceType, password, deviceId,loginType)
        return call
    }

    fun logout(
            userId: String, deviceId: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.doLogout(userId, deviceId)
        return call
    }


    fun forgotPassword(email: String): Call<ForgotPasswordModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.forgotPassword(email)
        return call
    }

    fun register(
            fullname: String,
            email: String,
            password: String,
            contact: String,
            deviceType: String,
            deciceToken: String,
            fcmToken: String,
            country: String,
            countryIso: String,
            deviceId: String
    ): Call<RegisterResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.register(fullname, email, password, contact, deviceType, deciceToken, fcmToken, country, countryIso, deviceId)
        return call
    }

    fun getProfile(userId: String, fromUserId: String, activity: Activity): Call<ProfileModel> {
        mAppContext = activity
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getProfile(userId, fromUserId)
        return call
    }

    fun getNotification(userId: String, pageNo: String, activity: FragmentActivity?): Call<NotificationModel> {
        mAppContext = activity
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientGet())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getNotification(userId, pageNo)
        return call
    }

    fun searchUser(
            userId: String,
            searchText: String,
            pageNo: String,
            activity: FragmentActivity
    ): Call<SearchUserModel> {

        mAppContext = activity

        val api = getClient()!!.create(Api::class.java)
        val call = api.searchUser(userId, searchText, pageNo)
        return call
    }


    fun exploreData(userId: String, pageNo: String, activity: FragmentActivity): Call<ExploreResponse> {
        this.mAppContext = activity
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientGet())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getExploreData(userId, pageNo)
        return call
    }

    fun getTrending(): Call<TrendingResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientGet())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getTrending()
        return call
    }

    fun getAllTrending(pageNo: String,type: String): Call<HomeFeedModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientGet())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getAllTrending(pageNo,type)
        return call
    }

    fun fetchFeed(userId: String, pageNo: String, deviceType: String, deciceToken: String, deviceId: String, fcmToken: String, activity: FragmentActivity): Call<HomeFeedModel> {
        this.mAppContext = activity
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientGet())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.fetchFeed(userId, pageNo, deviceType, deciceToken, deviceId, fcmToken)
        return call
    }


    fun sharePost(userId: String,postId: String,caption:String): Call<SharePostResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientGet())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.sharePost(userId, postId,caption)
        return call
    }

    fun fetchProfile(userId: String): Call<FetchProfileModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.fetchProfile(userId)
        return call
    }

    fun detailPage(postId: String, userId: String): Call<PostDetailModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.detailPage(postId, userId)
        return call
    }

    fun followUnfollow(userId: String, followerId: String, type: String): Call<FollowUnfollowModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.followUnfollow(userId, followerId, type)
        return call
    }

    fun updateProfiles(mParms: HashMap<String, RequestBody>, profilePic: MultipartBody.Part): Call<UpdateProfileModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.updateProfile(mParms, profilePic)
        return call
    }

    fun followList(id: String, search: String, type: String, pageNo: String, userId: String): Call<FollowListModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.followList(id, search, type, pageNo, userId)
        return call
    }


    fun fetchComments(postId: String): Call<GetCommentModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.fetchComment(postId)
        return call
    }

    fun postComment(userId: String, postId: String, commemt: String, tagUser: String): Call<PostCommentModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.postComment(userId, postId, commemt, tagUser)
        return call
    }

    fun uploadFeed(mParms: HashMap<String, RequestBody>, post: MultipartBody.Part): Call<UploadPostModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.uploadFeed(mParms, post)
        return call
    }

    fun updateProfilePic(
            mParms: HashMap<String, RequestBody>,
            post: MultipartBody.Part
    ): Call<UpdateProfilePicResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.updateProfilePic(mParms, post)
        return call
    }

    fun likePost(userId: String, postId: String, isLike: String): Call<LikeDislikeModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.postLikes(userId, postId, isLike)
        return call
    }

    fun addFriend(toUserId: String, fromUserId: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.addFriend(toUserId, fromUserId)
        return call
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.changePassword(userId, oldPassword, newPassword)
        return call
    }

    fun resetPassword(phone: String, password: String): Call<ResetPasswordResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.resetPassword(phone, password)
        return call
    }

    fun tagSuggestion(name: String): Call<TagSuggestion> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.tagSuggestion(name)
        return call
    }


    fun sendNotification(token: String, request: NotificationRequest): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_FOR_NOTI)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.fcmNotification(token, request)
        return call
    }

    fun blockUnblock(toUserId: String, fromUserId: String, isBlock: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.blockUnblock(toUserId, fromUserId, isBlock)
        return call
    }

    fun genrateTicket(userId: String, query: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.genrateTicket(userId, query)
        return call
    }

    fun fetchTickets(userId: String): Call<FetchTicketsResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.fetchTicket(userId)
        return call
    }

    fun sendMsgToSupport(userId: String, queryId: String, query: String): Call<SupportChatResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.sendMsgToSupport(userId, queryId, query)
        return call
    }

    fun fetchUserChat(queryId: String): Call<FetchChatResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.fetchUserChat(queryId)
        return call
    }

    fun getReportReason(): Call<ReportReasonesponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getReportReasons()
        return call
    }

    fun addReportReasons(
            reasonId: String,
            toUserId: String,
            postId: String,
            fromUserId: String
    ): Call<ReportReasonesponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.addReportReasons(reasonId, toUserId, postId, fromUserId)
        return call
    }

    fun getBlockList(userId: String): Call<BlockUserResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getBlockList(userId)
        return call
    }

    fun deletePost(postId: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.deletePost(postId)
        return call
    }


    fun userIsExist(email: String, contact: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.userIsExist(email, contact)
        return call
    }

    fun postView(userId: String, postId: String): Call<CommonModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.postView(userId, postId)
        return call
    }

    fun getWaterMarkVideo(url: String): Call<WatermarkVideoModel> {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpClientPost())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getWaterMarkVideoUrl(url)
        return call
    }

    private fun getOkHttpClientPost(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.interceptors().add(httpLoggingInterceptor)
        builder.connectTimeout(1, TimeUnit.MINUTES)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        return builder.build()
    }

    private fun getOkHttpClientGet(): OkHttpClient {

        val httpCacheDirectory = File(mAppContext?.cacheDir, "offlineCache")
        //10 MB
        val cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.interceptors().add(httpLoggingInterceptor)
        builder.addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
        builder.addInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
       //builder.cache(cache)
        builder.connectTimeout(1, TimeUnit.MINUTES)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        return builder.build()
    }

    private val REWRITE_RESPONSE_INTERCEPTOR = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())
            val cacheControl = originalResponse.header("Cache-Control")
            return if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")
            ) {
                originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + 5000)
                        .build()
            } else {
                originalResponse
            }
        }
    }

    private val REWRITE_RESPONSE_INTERCEPTOR_OFFLINE = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            /*
                *  Leveraging the advantage of using Kotlin,
                *  we initialize the request and change its header depending on whether
                *  the device is connected to Internet or not.
                */
            request = if (hasNetwork(mAppContext!!)!!)
            /*
            *  If there is Internet, get the cache that was stored 5 seconds ago.
            *  If the cache is older than 5 seconds, then discard it,
            *  and indicate an error in fetching the response.
            *  The 'max-age' attribute is responsible for this behavior.
            */
                request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
            else
            /*
            *  If there is no Internet, get the cache that was stored 7 days ago.
            *  If the cache is older than 7 days, then discard it,
            *  and indicate an error in fetching the response.
            *  The 'max-stale' attribute is responsible for this behavior.
            *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
            */
                request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                ).build()
            return chain.proceed(request)
        }
    }

    fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }


}