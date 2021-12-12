package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingViewModel(sharedPref : SharedPref) :  ViewModel() {

    val sharedPref: SharedPref = sharedPref
    var respositry: WebServices = WebServices()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoader : MutableLiveData<Boolean>?= MutableLiveData()
    var logoutResponse : MutableLiveData<CommonModel> = MutableLiveData()

    fun doLogout(userId: String, deviceId: String) {
        isLoader?.value=true
        respositry.logout(userId,deviceId).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoader?.value=false
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                isLoader?.value=false
                logoutResponse.value=response.body()
            }
        })
    }

    fun back(view:View){
        view.findNavController().popBackStack()

    }

    fun support(view: View){
        view.findNavController().navigate(R.id.action_settingFragmnet_to_supportFragment)
    }

    fun changePassword(view: View){
        view.findNavController().navigate(R.id.action_settingFragmnet_to_changePasswordFragment)
    }

    fun blocked(view: View){
        view.findNavController().navigate(R.id.action_settingFragmnet_to_blockedUserFragment)
    }
}