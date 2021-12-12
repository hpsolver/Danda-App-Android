package com.sammyekaran.danda.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.getWatermarkUrl.WatermarkVideoModel
import com.sammyekaran.danda.model.likedislike.LikeDislikeModel
import com.sammyekaran.danda.model.postcomment.PostCommentModel
import com.sammyekaran.danda.model.postdetail.PostDetailModel
import com.sammyekaran.danda.model.splitPayment.SplitPaymentResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.fragment.PostDetailFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailViewModel(sharedPref: SharedPref) : ViewModel() {

    lateinit var isLike: String
    var respositry: WebServices
    var detail: MutableLiveData<PostDetailModel>? = MutableLiveData()
    var deletePost: MutableLiveData<CommonModel>? = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var postViewResponse: MutableLiveData<CommonModel>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var donate: MutableLiveData<SplitPaymentResponse>? = MutableLiveData()
    var sharedPref: SharedPref

    init {
        this.respositry = WebServices()
        this.sharedPref = sharedPref
    }

    fun viewDownload(view: View, context: Context, postUrl: String, postsType: String) {
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

    fun postDetail(postId: String,userId: String) {
        isLoading.value = true
        respositry.detailPage(postId,userId).enqueue(object : Callback<PostDetailModel> {
            override fun onFailure(call: Call<PostDetailModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<PostDetailModel>, response: Response<PostDetailModel>) {
                isLoading.value = false
                if (response.body() != null)
                    detail!!.value = response.body()

            }
        })
    }
    fun postView(postId: String) {
        respositry.postView(sharedPref.getString(Constants.USER_ID), postId).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                postViewResponse?.value = response.body()
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

    fun deletePost(postId: String) {
        isLoading.value = true
        respositry.deletePost(postId).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                isLoading.value = false
                if (response.body() != null)
                    deletePost!!.value = response.body()

            }
        })
    }

    fun likeDislike(userId: String, postId: String, type: String) {
        respositry.likePost(userId, postId, type).enqueue(object : Callback<LikeDislikeModel> {
            override fun onFailure(call: Call<LikeDislikeModel>, t: Throwable) {
                //errorListener!!.value = t.message!!
            }

            override fun onResponse(call: Call<LikeDislikeModel>, response: Response<LikeDislikeModel>) {
                //likeDislike.value = response.body()

            }
        })
    }

    private fun downloadFile(view: View,context: Context, postUrl: String, fileName: String) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(
                Uri.parse(postUrl))
                .setTitle(fileName)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        dm.enqueue(request)

        CommonUtils().showMessage(context,view,"Downloading Start");

    }


    fun back(view: View) {
        view.findNavController().popBackStack()
    }


    fun comment(view: View, postId: String) {
        val action = PostDetailFragmentDirections.actionPostDetailFragmentToCommentFragment(postId)
        view.findNavController().navigate(action)
    }

    fun getProfile(view: View, userId: String) {
        val action = PostDetailFragmentDirections.actionPostDetailFragmentToProfileFragment(userId)
        view.findNavController().navigate(action)
    }
}