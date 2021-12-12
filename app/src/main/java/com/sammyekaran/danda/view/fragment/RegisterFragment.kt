package com.sammyekaran.danda.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentRegisterBinding
import com.sammyekaran.danda.model.register.Data
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.utils.Validations
import com.sammyekaran.danda.view.activity.VerifyPhoneActivity
import com.sammyekaran.danda.viewmodel.RegisterViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.editTextEmail
import kotlinx.android.synthetic.main.fragment_register.editTextName
import kotlinx.android.synthetic.main.fragment_register.editTextPhoneNumber
import kotlinx.android.synthetic.main.fragment_register.textViewCountry
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {


    private val VERIFICATION_REQUEST_CODE = 100
    lateinit var mRegisterBinding: FragmentRegisterBinding
    val registerViewModel: RegisterViewModel by viewModel()
    val sharedPref: SharedPref by inject()
    val validations: Validations by inject()
    val commonUtils: CommonUtils by inject()
    var mFCMToken = ""
    var phoneNumber = ""


    companion object {
        var sCountryCode = ""
        var sIso = ""
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_register
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tm = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso
        if (countryCodeValue == "ken") {
            sIso = "KEN"
            sCountryCode = "254"
        } else {
            sIso = "KEN"
            sCountryCode = "254"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRegisterBinding = getViewDataBinding()
        mRegisterBinding.viewModel = registerViewModel
        if (sIso.isNotEmpty())
            textViewCountry.text = sIso + " + " + sCountryCode
        listener()

        cbTermAndCondition.text = ""
        tvTermCondition.text = Html.fromHtml(
                "<font color='#000000'>I have read and agree to the </font>" +
                        "<font color='#FF3800'><a href='id.web.termsandcondition.webviewactivity://Kode'>TERMS AND CONDITIONS</a></font>"
        )
        tvTermCondition.isClickable = true
        tvTermCondition.movementMethod = LinkMovementMethod.getInstance()

        getFirebaseToken()
        setUpObserver()
    }

    private fun listener() {
        buttonSignUp.setOnClickListener {
            //Hide Keyboard
            commonUtils.hideSoftKeyBoard(activity!!)

            phoneNumber = editTextPhoneNumber.text.toString()
            val re = Regex("[^0-9]")
            phoneNumber = re.replace(phoneNumber, "")

            if (validations.isEmpty(editTextName.text.toString())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please enter name.")
            } else if (!validations.isValid(editTextName.text.toString())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Enter valid name.")
            } else if (validations.isEmpty(editTextEmail.text.toString())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please enter E-mail Id.")
            } else if (!validations.isValidEmail(editTextEmail.text.toString())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please enter valid E-mail Id.")
            } else if (validations.isEmpty(sCountryCode)) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please select country.")
            } else if (validations.isEmpty(phoneNumber.trim())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please enter phone number.")
            } else if (!validations.isValidMobile(phoneNumber.trim())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please enter valid phone number.")
            } else if (validations.isEmpty(editTextPassword.text.toString())) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please enter password.")
            } else if (editTextPassword.text.toString().length < 6) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Password must be at least six digit.")
            } else if (!cbTermAndCondition.isChecked) {
                baseshowFeedbackMessage(rootLayoutSignUp, "Please Accept Term and Conditions.")
            } else {
                mRegisterBinding.buttonSignUp.isEnabled = false
                registerViewModel.userIsExist(editTextEmail.text.toString(), phoneNumber)
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun setUpObserver() {

        registerViewModel.registerResponse!!.observe(viewLifecycleOwner, Observer { data ->
            mRegisterBinding.buttonSignUp.isEnabled = true
            if (data?.response?.status.equals("0")) {
                baseshowFeedbackMessage(rootLayoutSignUp, data?.response?.message!!)
            } else {
                addUserInFireStore(data?.response?.data)
                sharedPref.setBoolean(Constants.IS_LOGIN, true)
                sharedPref.setString(Constants.USER_ID, data.response?.data?.user_id!!)
                sharedPref.setString(Constants.FULL_NAME, editTextName.text.toString())
                sharedPref.setString(Constants.EMAIL, editTextEmail.text.toString())
                findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
            }
        })

        registerViewModel.errorListener!!.observe(viewLifecycleOwner, Observer { message ->
            mRegisterBinding.buttonSignUp.isEnabled = true
            baseshowFeedbackMessage(rootLayoutSignUp, message!!)
        })

        registerViewModel.isUserExist!!.observe(viewLifecycleOwner, Observer { it ->
            mRegisterBinding.buttonSignUp.isEnabled = true
            if (it?.response?.status.equals("1")) {
                verifyPhoneNumber()
            } else {
                baseshowFeedbackMessage(rootLayoutSignUp, it?.response?.message!!)
            }
        })


        registerViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                baseShowProgressBar("Wait...")
            } else {
                baseHideProgressDialog()
            }
        })
    }


    private fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult> {
                    override fun onComplete(task: Task<InstanceIdResult>) {
                        if (!task.isSuccessful) {
                            return
                        }
                        // Get new Instance ID token
                        mFCMToken = task.result?.token!!
                    }

                })
    }

    private fun addUserInFireStore(data: Data?) {
        val userData = hashMapOf(
                "fcmToken" to mFCMToken,
                "active" to true,
                "fullName" to editTextName.text.toString(),
                "pic" to "",
                "userId" to data?.user_id,
                "userName" to data?.username,
                "chatWith" to ""
        )

        FirebaseFirestore.getInstance().collection("AllUsersInfo").document(data?.user_id!!)
                .set(userData)
    }

    private fun verifyPhoneNumber() {
        startActivityForResult(Intent(activity, VerifyPhoneActivity::class.java)
                .putExtra("countryCode", sCountryCode)
                .putExtra("mobile", phoneNumber), VERIFICATION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VERIFICATION_REQUEST_CODE) {
                completeRegistration()
            }
        }
    }

    private fun completeRegistration() {
        registerViewModel.register(
                editTextName.text.toString(),
                editTextEmail.text.toString(),
                editTextPassword.text.toString(),
                phoneNumber,
                "A",
                mFCMToken,
                mFCMToken,
                sCountryCode,
                sIso,
                Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        registerViewModel.isUserExist?.removeObservers(activity!!)
        registerViewModel.errorListener?.removeObservers(activity!!)
    }
}
