package com.sammyekaran.danda.view.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.SettingFragmnetBinding
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.viewmodel.SettingViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.setting_fragmnet.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SettingFragmnet : BaseFragment<SettingFragmnetBinding>() {

    lateinit var binding: SettingFragmnetBinding
    val viewModel: SettingViewModel by viewModel()
    val sharedPref: SharedPref by inject()

    override fun getLayoutId(): Int {
        return R.layout.setting_fragmnet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel

        listner()


    }

    private fun listner() {
        relativeLayoutInvite.setOnClickListener {
            shareIntent()
        }
        rlLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun shareIntent() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=com.sammyekaran.danda"
        )
        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {

        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayout, message!!)
            }
        })

        viewModel.logoutResponse.observe(viewLifecycleOwner, object : Observer<CommonModel> {
            override fun onChanged(t: CommonModel?) {
                if (t?.response?.status.equals("0")) {
                    baseshowFeedbackMessage(rootLayout, t?.response?.message!!)
                } else {
                    clearLocalData()
                }
            }
        })


        viewModel.isLoader?.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("Loging off...")
                } else {
                    baseHideProgressDialog()
                }
            }
        })
    }

    fun showLogoutDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setTitle("Logout ?")
        dialogBuilder.setMessage("Do you want to logout from the App ?")
        dialogBuilder.setPositiveButton("Yes") { dialog: DialogInterface, which ->
            dialog.dismiss()
            viewModel.doLogout(sharedPref.getString(Constants.USER_ID),
                Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID))
        }
        dialogBuilder.setNegativeButton("No") { dialog: DialogInterface, which -> dialog.dismiss() }
        dialogBuilder.show()

    }

    private fun clearLocalData() {
        FirebaseFirestore.getInstance().collection("AllUsersInfo").document(sharedPref.getString(Constants.USER_ID))
            .update("fcmToken", "")
        sharedPref.setBoolean(Constants.IS_LOGIN, false)
        sharedPref.setString(Constants.USER_ID, "")
        sharedPref.setString(Constants.FULL_NAME, "")
        findNavController().navigate(R.id.action_settingFragmnet_to_loginFragment)
    }


}