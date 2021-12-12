package com.sammyekaran.danda.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.model.notification.Datum
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.NotificationFragmentDirections

class NotificationViewModel : ViewModel() {

    var respositry: WebServices
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        this.respositry = WebServices()
    }


    fun navigate(view: View, data: Datum) {

        if (data.postId.equals("0")) {
            val action =
                NotificationFragmentDirections.actionNotificationFragmentToProfileFragment(data.userId!!)
            view.findNavController().navigate(action)
        } else {
            val action =
                NotificationFragmentDirections.actionNotificationFragmentToPostDetailFragment(data.postId!!)
            view.findNavController().navigate(action)
        }
    }

    fun navigateToProfile(view: View, data: Datum) {
        val action =
            NotificationFragmentDirections.actionNotificationFragmentToProfileFragment(data.userId!!)
        view.findNavController().navigate(action)

    }

    fun navigateToNotificationDetail(view: View, noti: String) {
        val action =
            NotificationFragmentDirections.actionNotificationFragmentToNotificationDetailFragment(noti)
        view.findNavController().navigate(action)

    }

}