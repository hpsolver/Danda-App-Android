package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.register.RegisterResponse
import com.sammyekaran.danda.repositry.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel() : ViewModel() {

    var respositry: WebServices = WebServices()
    var registerResponse: MutableLiveData<RegisterResponse>? = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isUserExist: MutableLiveData<CommonModel>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()


    fun register(
        name: String,
        email: String,
        password: String,
        contact: String,
        deviceType: String,
        deciceToken: String,
        fcmToken: String,
        countoryCode: String,
        countoryIso: String,
        deviceId:String
    ) {
        isLoading.value = true
        respositry.register(name, email, password, contact, deviceType, deciceToken, fcmToken, countoryCode,countoryIso,deviceId)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    errorListener!!.value = t.message!!
                    isLoading.value = false
                }

                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        registerResponse!!.value = response.body()
                    } else {
                        errorListener!!.value = response.errorBody().toString()
                    }

                }
            })
    }

    fun userIsExist(email: String, contact: String) {
        isLoading.value = true
        respositry.userIsExist(email, contact)
            .enqueue(object : Callback<CommonModel> {
                override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                    errorListener!!.value = t.message!!
                    isLoading.value = false
                }

                override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                    isLoading.value = false
                    isUserExist?.value = response.body()


                }
            })
    }

    fun onBackClick(view: View) {
        view.findNavController().popBackStack()
    }

    fun onLoginClick(view: View) {
        view.findNavController().popBackStack()
    }

    fun seclectCountry(view: View) {
        view.findNavController().navigate(R.id.action_registerFragment_to_countryListFragment)
    }


}
