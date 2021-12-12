package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.searchuser.Datum
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.fragment.SearchFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(sharedPref: SharedPref) : ViewModel() {

    var respositry: WebServices = WebServices()
    var sharedPref: SharedPref = sharedPref
    var followUnfollow: MutableLiveData<FollowUnfollowModel> = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var followUnfollowStatus: MutableLiveData<Datum> = MutableLiveData()



     fun followUnfollow(userId: String, followerId: String, type: String) {
        isLoading.value = true
        respositry.followUnfollow(followerId,userId,type).enqueue(object : Callback<FollowUnfollowModel> {
            override fun onFailure(call: Call<FollowUnfollowModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<FollowUnfollowModel>, response: Response<FollowUnfollowModel>) {
                followUnfollow.value = response.body()
                isLoading.value = false
            }
        })
    }





    fun follow(view: View, search: Datum) {
        followUnfollowStatus.value = search

    }



    fun getProfile(view: View, userId: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment(userId)
        view.findNavController().navigate(action)

    }

}