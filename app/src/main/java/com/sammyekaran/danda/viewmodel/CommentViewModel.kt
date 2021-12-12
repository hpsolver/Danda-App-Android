package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.getcomment.GetCommentModel
import com.sammyekaran.danda.model.postcomment.PostCommentModel
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.CommentFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentViewModel  : ViewModel() {

    var respositry: WebServices
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var allComments: MutableLiveData<GetCommentModel> = MutableLiveData()
    var postComment: MutableLiveData<PostCommentModel> = MutableLiveData()
    var tagSuggestion: MutableLiveData<TagSuggestion> = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()

    init {
        this.respositry= WebServices()
    }


    fun fetchComments(postId: String) {
        isLoading.value = true
        respositry.fetchComments(postId).enqueue(object : Callback<GetCommentModel> {
            override fun onFailure(call: Call<GetCommentModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<GetCommentModel>, response: Response<GetCommentModel>) {
                allComments.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun postComment(userId: String, postId: String, comment: String, tagUser: String) {
        isLoading.value = true
        respositry.postComment(userId,postId,comment,tagUser).enqueue(object : Callback<PostCommentModel> {
            override fun onFailure(call: Call<PostCommentModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<PostCommentModel>, response: Response<PostCommentModel>) {
                postComment.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun tagSuggestion(name:String) {
        respositry.tagSuggestion(name).enqueue(object : Callback<TagSuggestion> {
            override fun onFailure(call: Call<TagSuggestion>, t: Throwable) {

            }

            override fun onResponse(call: Call<TagSuggestion>, response: Response<TagSuggestion>) {
                tagSuggestion.value = response.body()
            }
        })
    }

    fun onBackClick(view:View){
        view.findNavController().navigateUp()
    }

    fun getProfile(view: View,id:String){
        val  action= CommentFragmentDirections.actionCommentFragmentToProfileFragment(id)
        view.findNavController().navigate(action)
    }

    fun getAllPost():MutableLiveData<GetCommentModel>{
        return allComments
    }

}