package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.fcmnotification.NotificationRequest
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.FriendListFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsViewModel : ViewModel() {

    var respositry: WebServices
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var notistatus: MutableLiveData<CommonModel>? = MutableLiveData()

    init {
        this.respositry = WebServices()
    }


    fun senNotification(token: String, request: NotificationRequest) {
        isLoading.value = true
        respositry.sendNotification(token, request).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                notistatus!!.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun onBackClick(view: View) {
        view.findNavController().popBackStack()
    }


    fun navigateToChat(view: View, userId: String, token: String, userName: String) {
        val action = FriendListFragmentDirections.actionFriendListFragmentToChatFragment(userId, token, userName)
        view.findNavController().navigate(action)
    }

}