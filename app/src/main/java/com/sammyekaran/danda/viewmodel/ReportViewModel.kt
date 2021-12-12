package com.sammyekaran.danda.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.reportReason.ReportReasonesponse
import com.sammyekaran.danda.repositry.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportViewModel : ViewModel() {


    var respositry: WebServices
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var reportReasonesponse: MutableLiveData<ReportReasonesponse> = MutableLiveData()
    var reportStatus: MutableLiveData<ReportReasonesponse> = MutableLiveData()
    var blockUnblock: MutableLiveData<CommonModel> = MutableLiveData()

    init {
        this.respositry = WebServices()
    }


    fun getReportList() {
        isLoading.value = true
        respositry.getReportReason().enqueue(object : Callback<ReportReasonesponse> {
            override fun onFailure(call: Call<ReportReasonesponse>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<ReportReasonesponse>, response: Response<ReportReasonesponse>) {
                isLoading.value = false
                if (response.body() != null)
                    reportReasonesponse.value = response.body()

            }
        })
    }

    fun blockUnBlock(toUserId: String, fromUserID: String, type: String) {
        isLoading.value = true
        respositry.blockUnblock(toUserId, fromUserID, type).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                isLoading.value = false
                if (response.body() != null)
                    blockUnblock.value = response.body()
            }
        })
    }

    fun addReportReasons(reasonId:String,toUserId: String, postId: String, fromUserID: String) {
        isLoading.value = true
        respositry.addReportReasons(reasonId,toUserId, postId, fromUserID).enqueue(object : Callback<ReportReasonesponse> {
            override fun onFailure(call: Call<ReportReasonesponse>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<ReportReasonesponse>, response: Response<ReportReasonesponse>) {
                isLoading.value = false
                if (response.body() != null)
                    reportStatus.value = response.body()
            }
        })
    }

}