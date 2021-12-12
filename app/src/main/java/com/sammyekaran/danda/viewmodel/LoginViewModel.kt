package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.LoginModel
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.register.RegisterResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Validations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(validations: Validations) : ViewModel() {

    var respositry: WebServices = WebServices()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var validations: Validations = validations

    var isUserExist: MutableLiveData<CommonModel>? = MutableLiveData()
    var registerResponse: MutableLiveData<RegisterResponse>? = MutableLiveData()
    var userMutableLiveData: MutableLiveData<LoginModel>? = null
    var isLoader: MutableLiveData<Boolean>? = null

    fun getUser(): MutableLiveData<LoginModel> {

        if (userMutableLiveData == null) {
            userMutableLiveData = MutableLiveData()
        }
        return userMutableLiveData!!

    }

    fun isLoading(): MutableLiveData<Boolean> {

        if (isLoader == null) {
            isLoader = MutableLiveData<Boolean>()
        }
        return isLoader!!

    }


    fun login(
            email: String,
            deciceToken: String,
            fcmToken: String,
            deviceType: String,
            password: String,
            deviceId: String,
            loginType: String
    ) {
        isLoader?.value = true
        respositry.login(email, deciceToken, fcmToken, deviceType, password, deviceId,loginType).enqueue(object : Callback<LoginModel> {
            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                isLoader?.value = false
                errorListener!!.value = t.message!!

            }

            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                isLoader?.value = false
                getUser().value = response.body()
            }
        })
    }

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
            deviceId: String
    ) {
        isLoader?.value = true
        respositry.register(name, email, password, contact, deviceType, deciceToken, fcmToken, countoryCode, countoryIso, deviceId)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        errorListener!!.value = t.message!!
                        isLoader?.value = false
                    }

                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        isLoader?.value = false
                        if (response.isSuccessful) {
                            registerResponse!!.value = response.body()
                        } else {
                            errorListener!!.value = response.errorBody().toString()
                        }

                    }
                })
    }
    fun userIsExist(email: String, contact: String) {
        isLoader?.value = true
        WebServices().userIsExist(email, contact)
                .enqueue(object : Callback<CommonModel> {
                    override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                        errorListener!!.value = t.message!!
                        isLoader?.value = false
                    }

                    override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                        isLoader?.value = false
                        isUserExist?.value = response.body()

                    }
                })
    }

    fun getUserExist(): LiveData<CommonModel> {
        return  isUserExist!!
    }


    fun onForgotClick(view: View) {
        view.findNavController().navigate(com.sammyekaran.danda.R.id.action_login_to_forget)
    }


    fun onSignUpClick(view: View) {
        view.findNavController().navigate(com.sammyekaran.danda.R.id.action_login_to_register)
    }

}
