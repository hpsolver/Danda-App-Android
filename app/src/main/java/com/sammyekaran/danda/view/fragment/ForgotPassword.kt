package com.sammyekaran.danda.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseContext
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.utils.Validations
import com.sammyekaran.danda.viewmodel.ForgotViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.sammyekaran.danda.view.activity.VerifyPhoneActivity
import kotlinx.android.synthetic.main.fragment_forget.*
import java.util.concurrent.TimeUnit


class ForgotPassword : BaseFragmentWithoutBinding() {

    var forgotViewModel: ForgotViewModel? = null
    private val validations = Validations()
    private var mVerificationId = ""
    private var mAuth: FirebaseAuth? = null
    private val VERIFICATION_REQUEST_CODE=100


    companion object {
        var sCountryCode = ""
        var sIso = ""
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_forget
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tm = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso
        if (countryCodeValue == "ken"){
            sIso= "KEN"
            sCountryCode="254"
        }else{
            sIso= "KEN"
            sCountryCode="254"
        }
        mAuth = FirebaseAuth.getInstance()
        forgotViewModel = ViewModelProviders.of(this, BaseContext(mBaseActivity!!)).get(ForgotViewModel::class.java)
        setUpObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sIso.isNotEmpty())
            textViewCountry.text = sIso + " + " + sCountryCode
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener()
    }

    private fun listener() {

        imageViewBack.setOnClickListener {
            findNavController().popBackStack()
        }

        textViewCountry.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPassword_to_countryListFragment)
        }


        buttonSubmit.setOnClickListener {
            if (validations.isEmpty(sCountryCode)) {
                mBaseActivity?.showError("Please select country.")
            } else if (validations.isEmpty(editTextPhoneNumber.text.toString())) {
                mBaseActivity?.showError("Please enter  Mobile number.")
            } else if (!validations.isValidMobile(editTextPhoneNumber.text.toString())) {
                mBaseActivity?.showError("Please enter valid Mobile number.")
            } else {
                forgotViewModel?.userIsExist("", editTextPhoneNumber.text.toString())
            }
        }

    }

    private fun setUpObserver() {
        forgotViewModel?.getUserExist()?.observe(this, Observer {
            if (it?.response?.status.equals("0")) {
                startActivityForResult(Intent(activity, VerifyPhoneActivity::class.java)
                        .putExtra("countryCode", sCountryCode)
                        .putExtra("mobile",  editTextPhoneNumber.text.toString()), VERIFICATION_REQUEST_CODE)
            } else {
                mBaseActivity?.showError( "Phone number not exist !")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK){
            if (requestCode==VERIFICATION_REQUEST_CODE){
                val  action=ForgotPasswordDirections.actionForgotPasswordToResetPasswordFragment(editTextPhoneNumber.text.toString())
                findNavController().navigate(action)
            }
        }
    }


}