package com.sammyekaran.danda.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sammyekaran.danda.model.fetchprofile.FetchProfileModel
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.updateprofile.UpdateProfileModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Validations
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditViewModel(validations: Validations) : ViewModel() {

    var respositry: WebServices
    var fetchProfile: MutableLiveData<FetchProfileModel>? = MutableLiveData()
    var updateProfile: MutableLiveData<UpdateProfileModel>? = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    var isUserExist: MutableLiveData<CommonModel>? = MutableLiveData()

    init {
        this.respositry = WebServices()
    }


    fun fetchProfile(userId: String) {
        isLoading.value = true
        respositry.fetchProfile(userId).enqueue(object : Callback<FetchProfileModel> {
            override fun onFailure(call: Call<FetchProfileModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<FetchProfileModel>, response: Response<FetchProfileModel>) {
                fetchProfile!!.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun updateProfiles(mParms: HashMap<String, RequestBody>, profilePic: MultipartBody.Part) {
        isLoading.value = true
        respositry.updateProfiles(mParms,profilePic).enqueue(object : Callback<UpdateProfileModel> {
            override fun onFailure(call: Call<UpdateProfileModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<UpdateProfileModel>, response: Response<UpdateProfileModel>) {
                isLoading.value = false
                updateProfile!!.value = response.body()

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




}
