package com.sammyekaran.danda.viewmodel

import android.text.Editable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.followlist.Detail
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.fragment.FollowUnfollowFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowUnfollowViewModel(sharedPref: SharedPref) :  ViewModel() {

    var respositry: WebServices
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var showFeedbackMessage: MutableLiveData<String>? = MutableLiveData()
    var followUnfollow: MutableLiveData<FollowUnfollowModel> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var followUnfllowStatus: MutableLiveData<Detail> = MutableLiveData()
    lateinit var user: Detail
    var followType = ""
    var sharedPref: SharedPref


    init {
        this.respositry = WebServices()
        this.sharedPref=sharedPref
    }



    fun followUnfollow(userId: String, followerId: String, type: String) {
        isLoading.value = true
        respositry.followUnfollow(userId, followerId, type).enqueue(object : Callback<FollowUnfollowModel> {
            override fun onFailure(call: Call<FollowUnfollowModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<FollowUnfollowModel>, response: Response<FollowUnfollowModel>) {
                isLoading.value = false
                if (response.body()!=null){
                    followUnfollow.value = response.body()
                    if (type.equals("2"))
                        user.usersType = "0"
                    else
                        user.usersType = "1"
                }

            }
        })
    }

    fun follow(view: View, user: Detail) {
        this.user=user
        followUnfllowStatus.value = user

    }


    fun afterTextChanged(s: Editable) {
        //SearchFragment.data.clear()
        //seacherUser(sharedPref.getString(Constants.USER_ID), s.toString(), "1")
    }

    fun getProfile(view: View, userId: String) {
        val action = FollowUnfollowFragmentDirections.actionFollowUnfollowFragmentToProfileFragment(userId)
        view.findNavController().navigate(action)

    }

}