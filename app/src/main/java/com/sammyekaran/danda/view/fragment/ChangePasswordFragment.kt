package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentChangePasswordBinding
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.viewmodel.ChangePasswordViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    lateinit var binding: FragmentChangePasswordBinding
    val viewModel: ChangePasswordViewModel by viewModel()
    val commonUtils: CommonUtils by inject()
    val sharedPref: SharedPref by inject()

    override fun getLayoutId(): Int {
        return R.layout.fragment_change_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
    }

    fun setUpObserver() {

        viewModel.changePasswordResponse!!.observe(
            viewLifecycleOwner,
            object : Observer<CommonModel> {
                override fun onChanged(data: CommonModel?) {
                    binding.buttonSubmit.isEnabled = true
                    commonUtils.hideSoftKeyBoard(activity!!)
                    if (data?.response?.status.equals("0")) {
                        baseshowFeedbackMessage(rootLayoutChangePass, data?.response?.message!!)
                    } else {
                        Toast.makeText(activity!!, data?.response?.message, Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }

                }
            })


        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                commonUtils.hideSoftKeyBoard(activity!!)
                binding.buttonSubmit.isEnabled = true
                baseshowFeedbackMessage(rootLayoutChangePass, message!!)
            }
        })


        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("Wait...")
                } else {
                    baseHideProgressDialog()
                }
            }
        })

        viewModel.showFeedbackMessage!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String) {
                baseshowFeedbackMessage(rootLayoutChangePass, message)
            }
        })
    }
}