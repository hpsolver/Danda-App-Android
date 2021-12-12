package com.sammyekaran.danda.viewmodel

import androidx.lifecycle.ViewModel
import com.sammyekaran.danda.base.BaseActivity
import com.sammyekaran.danda.utils.NetworkUtils
import com.justcodenow.operrwork.base.BaseProgressDialogFragment


open class BaseViewModel : ViewModel() {


    var mBaseProgressDialogFragment: BaseProgressDialogFragment? = null

    fun onProgressShow(activity: BaseActivity) {
        //Internet connection checking
        if (!NetworkUtils.isNetworkConnected(activity)) {activity.showError("Please Check Your Internet Connection")}

        mBaseProgressDialogFragment = BaseProgressDialogFragment().getInstance()
        if (!mBaseProgressDialogFragment!!.isVisible) {
            mBaseProgressDialogFragment!!.show(activity.supportFragmentManager, "ProgressDialog")
        }
    }

    fun onProgressHide() {
        try{
            if (mBaseProgressDialogFragment != null) {
                mBaseProgressDialogFragment!!.dismiss()
            } else {
                return
            }
        }catch (e:IllegalStateException){
            e.printStackTrace()
        }

    }


}