package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.utils.Validations
import com.sammyekaran.danda.viewmodel.ChangePasswordViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.fragment_reset_password.editTextPassword
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ResetPasswordFragment : BaseFragmentWithoutBinding() {

    val viewModel: ChangePasswordViewModel by viewModel()
    private val commonUtils: CommonUtils by inject()
    val sharedPref: SharedPref by inject()
    val validations = Validations()
    val args: ResetPasswordFragmentArgs by navArgs()

    override fun getLayoutId(): Int {
        return R.layout.fragment_reset_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener()
    }

    private fun listener() {
        imageViewBack.setOnClickListener {
            findNavController().popBackStack()
        }
        buttonSubmit.setOnClickListener {
            commonUtils.hideSoftKeyBoard(activity!!);
            if (validations.isEmpty(editTextPassword.text.toString())) {
                mBaseActivity?.showError("Please enter new password.")
            } else if (validations.isEmpty(editTextConfirmPassword.text.toString())) {
                mBaseActivity?.showError("Please enter confirm password.")
            } else if (editTextPassword.text.toString().length < 6) {
                mBaseActivity?.showError("Password must be at least six digit.")
            } else if (!validations.isEqual(editTextPassword.text.toString(), editTextConfirmPassword.text.toString())) {
                mBaseActivity?.showError("Password did't match.")
            } else {
                viewModel.resetPassword(args.phone, editTextPassword.text.toString())
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
    }

    private fun setUpObserver() {

        viewModel.resetPasswordResponse!!.observe(
                viewLifecycleOwner, Observer { data ->
            buttonSubmit.isEnabled = true
            commonUtils.hideSoftKeyBoard(activity!!)
            if (data?.response?.status!!.equals("0")) {
                baseshowFeedbackMessage(rootLayoutChangePass, data.response.message)
            } else {
                findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
            }
        })


        viewModel.errorListener!!.observe(viewLifecycleOwner, Observer<String> { message ->
            buttonSubmit.isEnabled = true
            baseshowFeedbackMessage(rootLayoutChangePass, message!!)
        })


        viewModel.isLoading.observe(viewLifecycleOwner, Observer<Boolean> { isLoading ->
            if (isLoading) {
                baseShowProgressBar("Wait...")
            } else {
                baseHideProgressDialog()
            }
        })

        viewModel.showFeedbackMessage!!.observe(viewLifecycleOwner, Observer<String> { message -> baseshowFeedbackMessage(rootLayoutChangePass, message) })
    }
}