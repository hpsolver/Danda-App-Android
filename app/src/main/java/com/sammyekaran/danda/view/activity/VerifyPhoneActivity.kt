package com.sammyekaran.danda.view.activity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager.BadTokenException
import android.widget.Toast
import com.google.android.exoplayer2.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseActivity
import com.sammyekaran.danda.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_verify_phone.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


class VerifyPhoneActivity : BaseActivity() {
    private var mVerificationId = ""
    private var mAuth: FirebaseAuth? = null
    var countryNameCode = ""
    val commonUtils: CommonUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        mAuth = FirebaseAuth.getInstance()
        val intent = intent
        val mobile = intent.getStringExtra("mobile")
        val countryCode = intent.getStringExtra("countryCode")
        if (countryCode != null) {
            if (mobile != null) {
                sendVerificationCode(countryCode, mobile)
            }
        }

        mAuth = FirebaseAuth.getInstance()
        if (intent.hasExtra("countryNameCode"))
            countryNameCode = intent.getStringExtra("countryNameCode")!!

        textViewMobile.append("+$countryCode $mobile")
        if (countryCode != null) {
            if (mobile != null) {
                sendVerificationCode(countryCode, mobile)
            }
        }


        otpView.setOtpCompletionListener { otp ->
            commonUtils.hideSoftKeyBoard(this);
            baseShowProgressBar("Sending...")
            verifyVerificationCode(otp)
        }

        findViewById<View>(R.id.tvResend).setOnClickListener {
            if (countryCode != null) {
                if (mobile != null) {
                    sendVerificationCode(countryCode, mobile)
                }
            }
            commonUtils.hideSoftKeyBoard(this);
        }

        imageViewBack.setOnClickListener {
            finish()

        }
    }

    private fun sendVerificationCode(countryCode: String, mobile: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+$countryCode$mobile",
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks)
    }


    //the callback to detect the verification status
    private val mCallbacks: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {


                runOnUiThread(Runnable {
                    try {
                        baseShowProgressBar("Verifying...")
                        otpView.setText(code)
                    } catch (e: BadTokenException) {
                        Log.e("WindowManagerBad ", e.toString())
                    }
                })


            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@VerifyPhoneActivity, e.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            Toast.makeText(this@VerifyPhoneActivity, getString(R.string.code_sent_successfully), Toast.LENGTH_SHORT).show()
            mVerificationId = s
        }
    }


    private fun verifyVerificationCode(code: String) {

        if (mVerificationId.isEmpty()) {
            baseHideProgressDialog()
            Snackbar.make(findViewById(R.id.rootLayout), "Invalid code entered...", Snackbar.LENGTH_LONG).show()
        } else {
            val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this@VerifyPhoneActivity, OnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    } else {
                        var message = "Somthing is wrong, we will fix it soon..."
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered..."
                        }
                        val snackbar = Snackbar.make(findViewById(R.id.rootLayout), message, Snackbar.LENGTH_LONG)
                        snackbar.setAction("Dismiss") { v: View? -> }
                        snackbar.show()
                    }
                })
    }
}