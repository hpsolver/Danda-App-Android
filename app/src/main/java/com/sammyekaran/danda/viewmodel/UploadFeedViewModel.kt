package com.sammyekaran.danda.viewmodel

import android.text.Editable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.ProductLinkBean
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.model.uploadPost.UploadPostModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.UploadFeedFragment
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadFeedViewModel : ViewModel() {

    var respository: WebServices
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var showFeedbackMessage: MutableLiveData<String>? = MutableLiveData()
    var feedUploaded: MutableLiveData<UploadPostModel>? = MutableLiveData()
    var tagSuggestion: MutableLiveData<TagSuggestion>? = MutableLiveData()


    init {
        this.respository = WebServices()
    }

    fun onBackClick(view: View) {
        view.findNavController().popBackStack()
    }

    fun uploadFeed(mParms: HashMap<String, RequestBody>, post: MultipartBody.Part) {
        isLoading.value = true
        respository.uploadFeed(mParms, post).enqueue(object : Callback<UploadPostModel> {
            override fun onFailure(call: Call<UploadPostModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<UploadPostModel>, response: Response<UploadPostModel>) {
                feedUploaded!!.value = response.body()
                isLoading.value = false
            }
        })
    }

    fun tagSuggestion(name:String) {
        respository.tagSuggestion(name).enqueue(object : Callback<TagSuggestion> {
            override fun onFailure(call: Call<TagSuggestion>, t: Throwable) {

            }

            override fun onResponse(call: Call<TagSuggestion>, response: Response<TagSuggestion>) {
                tagSuggestion!!.value = response.body()
            }
        })
    }


}