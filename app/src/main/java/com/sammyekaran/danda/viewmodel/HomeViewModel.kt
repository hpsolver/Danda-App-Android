package com.sammyekaran.danda.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.getWatermarkUrl.WatermarkVideoModel
import com.sammyekaran.danda.model.homefeed.Result
import com.sammyekaran.danda.model.likedislike.LikeDislikeModel
import com.sammyekaran.danda.model.postcomment.PostCommentModel
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.view.fragment.MainFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel(context: Context) : ViewModel() {

    val context:Context=context
    var respositry: WebServices = WebServices()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var followUnfollow: MutableLiveData<FollowUnfollowModel> = MutableLiveData()
    var tagSuggestion: MutableLiveData<TagSuggestion> = MutableLiveData()
    var postViewResponse: MutableLiveData<CommonModel> = MutableLiveData()
    var isLike = "0"
    lateinit var feed: Result


    fun followUnfollow(userId: String, followerId: String, type: String) {
        respositry.followUnfollow(userId, followerId, type).enqueue(object : Callback<FollowUnfollowModel> {
            override fun onFailure(call: Call<FollowUnfollowModel>, t: Throwable) {
                errorListener!!.value = t.message!!
            }

            override fun onResponse(call: Call<FollowUnfollowModel>, response: Response<FollowUnfollowModel>) {
                followUnfollow.value = response.body()
            }
        })
    }

    fun postView(postId: String, userId: String) {
        respositry.postView(userId, postId).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                postViewResponse.value = response.body()
            }
        })
    }

    fun viewDownload(view: View,context: Context, postUrl: String, postsType: String) {
        val fileSeperator: List<String> = postUrl.split("/")
        val fileName = fileSeperator.get(fileSeperator.size - 1)
        if (postsType.equals("V")) {
            isLoading.value = true
            respositry.getWaterMarkVideo(fileName).enqueue(object : Callback<WatermarkVideoModel> {
                override fun onFailure(call: Call<WatermarkVideoModel>, t: Throwable) {
                    isLoading.value = false
                }

                override fun onResponse(call: Call<WatermarkVideoModel>, response: Response<WatermarkVideoModel>) {
                    isLoading.value = false
                    val fileSeperator: List<String> = response.body()?.response?.url!!.split("/")
                    val fileName = fileSeperator.get(fileSeperator.size - 1)
                    downloadFile(view,context, response.body()?.response?.url!!, fileName)
                }
            })
        } else {
            downloadFile(view,context, postUrl, fileName)
        }
    }

    private fun downloadFile(view: View,context: Context, postUrl: String, fileName: String) {
        val dm = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(
                Uri.parse(postUrl))
                .setTitle(fileName)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        dm.enqueue(request)

        CommonUtils().showMessage(context,view,"Downloading Start");

    }

    fun likeDislike(userId: String, postId: String, type: String) {
        respositry.likePost(userId, postId, type).enqueue(object : Callback<LikeDislikeModel> {
            override fun onFailure(call: Call<LikeDislikeModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<LikeDislikeModel>, response: Response<LikeDislikeModel>) {

            }
        })
    }

    fun postComment(userId: String, postId: String, comment: String) {
        isLoading.value = true
        respositry.postComment(userId, postId, comment, "").enqueue(object : Callback<PostCommentModel> {
            override fun onFailure(call: Call<PostCommentModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<PostCommentModel>, response: Response<PostCommentModel>) {
                isLoading.value = false
            }
        })
    }

    fun viewProfile(view: View, userId: String) {
        val action = MainFragmentDirections.actionMainFragmentToProfileFragment(userId)
        view.findNavController().navigate(action)


    }

    fun viewComment(view: View, postId: String) {
        val action = MainFragmentDirections.actionMainFragmentToCommentFragment(postId)
        view.findNavController().navigate(action)


    }

    fun likeDislikeClick(
            view: View,
            feed: Result,
            textViewLikes: TextView,
            userId: String
    ) {
        this.feed = feed
        var count = 0
        val imageView = view as ImageView

        if (feed.likes.equals("0")) {
            isLike = "1"
            count = feed.likesCount?.toInt()!! + 1
            imageView.setImageResource(R.drawable.ic_icon_like_red)
            if (feed.postsType.equals("i",true)){
                textViewLikes.text = count.toString() + " Likes"
            }

        } else {
            count = feed.likesCount?.toInt()!! - 1
            imageView.setImageResource(R.drawable.ic_icon_like)
            isLike = "0"
            if (feed.postsType.equals("i",true))
                textViewLikes.text = count.toString() + " Likes"
        }
        feed.likesCount = count.toString()
        feed.likes = isLike
        likeDislike(userId, feed.postId!!, isLike)
    }

    fun report(view: View?, postId: String?, userId: String?) {
        val action = MainFragmentDirections.actionMainFragmentToReportFragment(postId!!, userId!!)
        view?.findNavController()?.navigate(action)
    }



}