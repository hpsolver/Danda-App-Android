package com.sammyekaran.danda.viewmodel

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.profile.Data
import com.sammyekaran.danda.model.profile.ProfileModel
import com.sammyekaran.danda.model.saveStripeId.SaveStripeId
import com.sammyekaran.danda.model.splitPayment.SplitPaymentResponse
import com.sammyekaran.danda.model.updateProfilePic.UpdateProfilePicResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.view.fragment.EditProfileFragment
import com.sammyekaran.danda.view.fragment.ProfileFragmentDirections
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {

    var respositry: WebServices
    var profileData: MutableLiveData<ProfileModel>? = MutableLiveData()
    var errorListener: MutableLiveData<String>? = MutableLiveData()
    var updateProfilePicResponse: MutableLiveData<UpdateProfilePicResponse>? = MutableLiveData()
    var followUnfollow: MutableLiveData<FollowUnfollowModel>? = MutableLiveData()
    var blockUnblock: MutableLiveData<CommonModel>? = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var showFeedbackMessage: MutableLiveData<String>? = MutableLiveData()

    init {
        this.respositry = WebServices()
    }

    fun getProfile(userId: String, fromUserId: String, activity: Activity) {
        isLoading.value = true
        respositry.getProfile(userId, fromUserId,activity).enqueue(object : Callback<ProfileModel> {
            override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<ProfileModel>, response: Response<ProfileModel>) {
                isLoading.value = false
                if (response.body() != null)
                    profileData!!.value = response.body()

            }
        })
    }

    fun updateProfilePic(mParms: HashMap<String, RequestBody>, profilePic: MultipartBody.Part) {
        isLoading.value = true
        respositry.updateProfilePic(mParms, profilePic).enqueue(object : Callback<UpdateProfilePicResponse> {
            override fun onFailure(call: Call<UpdateProfilePicResponse>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<UpdateProfilePicResponse>, response: Response<UpdateProfilePicResponse>) {
                updateProfilePicResponse!!.value = response.body()
                isLoading.value = false
            }
        })
    }


    fun followUnfollow(userId: String, followerId: String, type: String) {
        isLoading.value = true
        respositry.followUnfollow(userId, followerId, type).enqueue(object : Callback<FollowUnfollowModel> {
            override fun onFailure(call: Call<FollowUnfollowModel>, t: Throwable) {
                errorListener!!.value = t.message!!
                isLoading.value = false
            }

            override fun onResponse(call: Call<FollowUnfollowModel>, response: Response<FollowUnfollowModel>) {
                followUnfollow?.value = response.body()
                isLoading.value = false
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
                    blockUnblock?.value = response.body()
            }
        })
    }


    fun followerClick(view: View, userId: String) {
        val action = ProfileFragmentDirections.actionProfileFragmentToFollowUnfollowFragment(userId, "1")
        view.findNavController().navigate(action)
    }


    fun followingClick(view: View, userId: String) {
        val action = ProfileFragmentDirections.actionProfileFragmentToFollowUnfollowFragment(userId, "2")
        view.findNavController().navigate(action)
    }

    fun postItemClick(view: View, id: String) {
        val action = ProfileFragmentDirections.actionProfileFragmentToPostDetailFragment(id)
        view.findNavController().navigate(action)

    }

    fun editProfile(view: View) {
        EditProfileFragment.isApiHit = false
        view.findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
    }


    fun message(view: View, data:Data) {
        EditProfileFragment.isApiHit = false
        view.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChatFragment(data.userId!!, data.fcmToken!!,data.username!!,
            data.name!!,data.profilePic!!,data.fromFullname!!))
    }

    fun back(view: View) {
        view.findNavController().popBackStack()
    }

    fun openSetting(view: View) {
        view.findNavController().navigate(R.id.action_profileFragment_to_settingFragmnet)
    }



}