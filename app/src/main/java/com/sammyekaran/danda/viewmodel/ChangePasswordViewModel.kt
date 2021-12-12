package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.resetResponse.ResetPasswordResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.utils.Validations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordViewModel(validations: Validations,sharedPref: SharedPref) : ViewModel() {

    val repository : WebServices
    var changePasswordResponse: MutableLiveData<CommonModel>? = MutableLiveData()
    var resetPasswordResponse: MutableLiveData<ResetPasswordResponse>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var showFeedbackMessage: MutableLiveData<String>? = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    val validations : Validations
    val sharedPref:SharedPref


    var mOldPassword: MutableLiveData<String>? = MutableLiveData()
    var mNewPassword: MutableLiveData<String>? = MutableLiveData()
    var mConfirmPassword: MutableLiveData<String>? = MutableLiveData()


    init {
        this.repository = WebServices()
        this.validations=validations
        this.sharedPref=sharedPref
    }

    fun changePassword(oldPassword:String,newPassword:String){
        isLoading.value = true
        repository.changePassword(sharedPref.getString(Constants.USER_ID), oldPassword, newPassword)
            .enqueue(object : Callback<CommonModel> {
                override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                    errorListener!!.value = t.message!!
                    isLoading.value = false
                }

                override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                   changePasswordResponse!!.value = response.body()
                    isLoading.value = false
                }
            })
    }

    fun resetPassword(phone:String,password:String){
        isLoading.value = true
        repository.resetPassword(phone,password)
            .enqueue(object : Callback<ResetPasswordResponse> {
                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                    errorListener!!.value = t.message!!
                    isLoading.value = false
                }

                override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                    resetPasswordResponse!!.value = response.body()
                    isLoading.value = false
                }
            })
    }

    fun back(view:View){
        view.findNavController().popBackStack()
    }

    fun onSubmit(view: View) {
        if (mOldPassword?.value == null || validations.isEmpty(mOldPassword?.value!!)) {
            showFeedbackMessage?.value = "Please enter old password."
        }else if (mNewPassword?.value == null || validations.isEmpty(mNewPassword?.value!!)) {
            showFeedbackMessage?.value = "Please enter new password."
        } else if (mNewPassword?.value!!.length < 6) {
            showFeedbackMessage?.value = "Password must be at least six digit."
        }else if (mConfirmPassword?.value == null || validations.isEmpty(mConfirmPassword?.value!!)) {
            showFeedbackMessage?.value = "Please enter confirm password."
        }else if (!validations.isEqual(mNewPassword?.value!!,mConfirmPassword?.value!!)){
            showFeedbackMessage?.value = "Password did't match."
        }else{

               changePassword(mOldPassword?.value!!,mConfirmPassword?.value!!)
        }
    }
}