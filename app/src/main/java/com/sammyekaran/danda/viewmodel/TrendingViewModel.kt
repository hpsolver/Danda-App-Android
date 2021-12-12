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
import com.sammyekaran.danda.model.trending.TrendingResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.view.fragment.MainFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TrendingViewModel(context: Context) : ViewModel() {

    val context:Context=context
    var respositry: WebServices = WebServices()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var trendingResponse: MutableLiveData<TrendingResponse> = MutableLiveData()



    fun getTrending() {
        respositry.getTrending().enqueue(object : Callback<TrendingResponse> {
            override fun onFailure(call: Call<TrendingResponse>, t: Throwable) {
                errorListener!!.value = t.message!!
            }

            override fun onResponse(call: Call<TrendingResponse>, response: Response<TrendingResponse>) {
                trendingResponse.value = response.body()
            }
        })
    }



    fun viewPostDetail(view: View, postId: String) {
        val action = MainFragmentDirections.actionMainFragmentToPostDetailFragment(postId)
        view.findNavController().navigate(action)


    }

    fun openAllTrendingFragment(view: View,type: String) {
        val action = MainFragmentDirections.actionMainFragmentToAllTrendingFragment(type)
        view.findNavController().navigate(action)
    }


}