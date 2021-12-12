package com.sammyekaran.danda.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sammyekaran.danda.base.BaseActivity
import com.sammyekaran.danda.model.ForgotPasswordModel
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.repositry.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotViewModel(var mBaseActivity: BaseActivity) : BaseViewModel() {

    var forgotResponse: MutableLiveData<ForgotPasswordModel>? = MutableLiveData()
    var isUserExist: MutableLiveData<CommonModel>? = MutableLiveData()


    fun forgotPassword(email: String) {
        onProgressShow(mBaseActivity)
        WebServices().forgotPassword(email).enqueue(object : Callback<ForgotPasswordModel> {
            override fun onFailure(call: Call<ForgotPasswordModel>, t: Throwable) {
                mBaseActivity.showApiError(t.message!!)
                onProgressHide()
            }

            override fun onResponse(call: Call<ForgotPasswordModel>, response: Response<ForgotPasswordModel>) {
                forgotResponse!!.value = response.body()
                onProgressHide()
            }
        })
    }

    fun userIsExist(email: String, contact: String) {
        onProgressShow(mBaseActivity)
        WebServices().userIsExist(email, contact)
                .enqueue(object : Callback<CommonModel> {
                    override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                        mBaseActivity.showApiError(t.message!!)
                        onProgressHide()
                    }

                    override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                        onProgressHide()
                        isUserExist?.value = response.body()

                    }
                })
    }


    fun getUserExist():LiveData<CommonModel>{
        return  isUserExist!!
    }
}