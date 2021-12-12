package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.sammyekaran.danda.base.BaseActivity
import com.sammyekaran.danda.model.exploreDataResponse.ExploreResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.ExploreFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreViewModel (var mBaseActivity: BaseActivity) :BaseViewModel() {

    var exploreResponse: MutableLiveData<ExploreResponse> = MutableLiveData()



    fun exploreData(userId: String,pageNo:String,activity: FragmentActivity?) {

        WebServices().exploreData(userId,pageNo,activity!!).enqueue(object : Callback<ExploreResponse> {
            override fun onFailure(call: Call<ExploreResponse>, t: Throwable) {
                mBaseActivity.baseHideProgressDialog()
                mBaseActivity.showApiError(t.message.toString())
            }

            override fun onResponse(call: Call<ExploreResponse>, response: Response<ExploreResponse>) {
                exploreResponse.value = response.body()
            }
        })
    }

    fun postItemClick(view : View, id: String) {
        val action = ExploreFragmentDirections.actionExploreDataFragmentToPostDetailFragment(id)
        view.findNavController().navigate(action)

    }

    fun  getExploreData():LiveData<ExploreResponse>{
        return exploreResponse
    }

}