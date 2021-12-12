package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.blockuserlist.BlockUserResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.BlockedUserFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockedViewModel : ViewModel() {

    var respositry: WebServices
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var blockUserResponse: MutableLiveData<BlockUserResponse> = MutableLiveData()

    init {
        this.respositry = WebServices()
    }

    fun getBlockedAccounts(userId: String) {
            isLoading.value = true
            respositry.getBlockList(userId).enqueue(object : Callback<BlockUserResponse> {
                override fun onFailure(call: Call<BlockUserResponse>, t: Throwable) {
                    errorListener!!.value = t.message!!
                    isLoading.value = false
                }

                override fun onResponse(call: Call<BlockUserResponse>, response: Response<BlockUserResponse>) {
                    isLoading.value = false
                    blockUserResponse.value = response.body()

                }
            })

        }

    fun getProfile(view: View, userId: String) {
        val action = BlockedUserFragmentDirections.actionBlockedUserFragmentToProfileFragment(userId)
        view.findNavController().navigate(action)

    }
}