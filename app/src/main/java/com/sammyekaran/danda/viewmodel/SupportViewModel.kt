package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.fetchsupportchat.FetchChatResponse
import com.sammyekaran.danda.model.fetchticket.FetchTicketsResponse
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.supportchat.SupportChatResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.SupportFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportViewModel : ViewModel() {


    var respositry: WebServices
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var genrateTicket: MutableLiveData<CommonModel> = MutableLiveData()
    var fetchTicketsResponse: MutableLiveData<FetchTicketsResponse> = MutableLiveData()
    var supportChatResponse: MutableLiveData<SupportChatResponse> = MutableLiveData()
    var fetchChatResponse: MutableLiveData<FetchChatResponse> = MutableLiveData()

    init {
        this.respositry = WebServices()
    }

    fun back(view:View){
        view.findNavController().popBackStack()
    }

    fun addTicket(view:View){
        view.findNavController().navigate(R.id.action_supportFragment_to_addTicketFragment)
    }

    fun genrateTicket(userId: String,query:String) {
        isLoading.value = true
        respositry.genrateTicket(userId,query).enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                genrateTicket.value = response.body()
                isLoading.value = false
            }
        })
    }


    fun fetchTickets(userId: String) {
        isLoading.value = true
        respositry.fetchTickets(userId).enqueue(object : Callback<FetchTicketsResponse> {
            override fun onFailure(call: Call<FetchTicketsResponse>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<FetchTicketsResponse>, response: Response<FetchTicketsResponse>) {
                fetchTicketsResponse.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun sendMsgToSupport(userId: String,queryId: String,query: String) {
        isLoading.value = true
        respositry.sendMsgToSupport(userId,queryId,query).enqueue(object : Callback<SupportChatResponse> {
            override fun onFailure(call: Call<SupportChatResponse>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<SupportChatResponse>, response: Response<SupportChatResponse>) {
                supportChatResponse.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun fetchUserChat(queryId: String) {
        isLoading.value = true
        respositry.fetchUserChat(queryId).enqueue(object : Callback<FetchChatResponse> {
            override fun onFailure(call: Call<FetchChatResponse>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<FetchChatResponse>, response: Response<FetchChatResponse>) {
                fetchChatResponse.value = response.body()
                isLoading.value = false
            }
        })
    }


    fun viewStatus(view: View,id: String){
        val  action = SupportFragmentDirections.actionSupportFragmentToSupportConversationFragment(id)
        view.findNavController().navigate(action)
    }



}