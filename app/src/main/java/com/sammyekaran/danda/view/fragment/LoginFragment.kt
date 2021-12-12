package com.sammyekaran.danda.view.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentLoginBinding
import com.sammyekaran.danda.model.LoginModel
import com.sammyekaran.danda.utils.*
import com.sammyekaran.danda.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.rootLayout
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val RC_SIGN_IN = 100
    private var phoneOrEmail = ""
    private var mUserName = ""
    private var TAG = LoginFragment::javaClass.name
    private var mFCMToken = ""
    lateinit var mViewDatBinding: FragmentLoginBinding
    val sharedPref: SharedPref by inject()
    private val commonUtils: CommonUtils by inject()
    private val validations: Validations by inject()
    val loginViewModel: LoginViewModel by viewModel()
    var deviceId = ""
    var loginType = ""
    var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private var callbackManager: CallbackManager? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDatBinding = getViewDataBinding()
        mViewDatBinding.viewModel = loginViewModel

        callbackManager = CallbackManager.Factory.create()
        mAuth = FirebaseAuth.getInstance()

        configureGoogleSignIn()

        listener()
        initObserver()
        getFirebaseToken()


    }

    private fun listener() {
        buttonLogin.setOnClickListener {
            loginType="emailLogin"
            commonUtils.hideSoftKeyBoard(activity!!)
            if (validations.isEmpty(editTextEmailOrPhone.text.toString().trim())) {
                baseshowFeedbackMessage(rootLayout, "Please enter E-mail Id or Mobile number.")
            } else if (!validateEmailOrPhone()) {
                baseshowFeedbackMessage(rootLayout, "Please enter valid E-mail Id or Mobile number.")
            } else if (validations.isEmpty(editTextPassword?.text.toString().trim())) {
                baseshowFeedbackMessage(rootLayout, "Please enter password.")
            } else {
                loginViewModel.login(phoneOrEmail,
                        mFCMToken,
                        mFCMToken,
                        "A",
                        editTextPassword.text.toString().trim(),
                        deviceId,loginType
                )
            }
        }

        ivGoogleSignin.setOnClickListener {
            commonUtils.hideSoftKeyBoard(activity!!)
            googleSignIn()
        }

        ivFacebookSignin.setOnClickListener {
            commonUtils.hideSoftKeyBoard(activity!!)
            facebookLogin()
        }


    }

    private fun facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        loginButton.setReadPermissions("email", "public_profile")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                Log.w(TAG, "FacebookException" + exception.message)
            }
        })
    }


    private fun configureGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity!!, gso)
    }

    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient?.getSignInIntent()!!
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    fun initObserver() {

        loginViewModel.getUser().observe(viewLifecycleOwner, Observer<LoginModel> { data ->
            mViewDatBinding.buttonLogin.isEnabled = true
            if (data.response?.status.equals("0")) {
                loginViewModel.isLoader?.value = false
                baseshowFeedbackMessage(rootLayout, data.response!!.message)
            } else {
                navigateToHome(data.response?.data?.email!!,data.response?.data?.userId!!,data.response?.data?.username!!,data.response?.data?.fullname!!,data.response?.data?.profile_pic!!)
            }
        })

        loginViewModel.getUserExist().observe(viewLifecycleOwner, Observer {
            if (it?.response?.status.equals("0")) {
                //social login api
                loginType="socialLogin"
                loginViewModel.login(phoneOrEmail,
                        mFCMToken,
                        mFCMToken,
                        "A",
                        editTextPassword.text.toString().trim(),
                        deviceId,loginType
                )
            } else {
                completeRegistration()
            }
        })

        loginViewModel.registerResponse!!.observe(viewLifecycleOwner, Observer { data ->
            if (data?.response?.status.equals("0")) {
                baseshowFeedbackMessage(rootLayout, data?.response?.message!!)
            } else {
                navigateToHome(phoneOrEmail, data.response?.data?.user_id!!, data.response.data.username!!, "", "")
            }
        })


        loginViewModel.errorListener!!.observe(viewLifecycleOwner, Observer<String> { message ->
            mViewDatBinding.buttonLogin.isEnabled = true
            baseshowFeedbackMessage(rootLayout, message!!)
        })


        loginViewModel.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { isLoading ->
            if (isLoading) {
                baseShowProgressBar("Loging in...")
            } else {
                baseHideProgressDialog()
            }
        })


    }

    private fun navigateToHome(email: String, userId: String, username: String, fullname: String, profilePic: String) {
        sharedPref.setBoolean(Constants.IS_LOGIN, true)
        sharedPref.setString(Constants.USER_ID, userId)
        sharedPref.setString(Constants.FULL_NAME,username)
        sharedPref.setString(Constants.EMAIL, email)

        addUserInFireStore(email,userId,username,fullname,profilePic)
        findNavController().navigate(R.id.action_login_to_main)
    }

    private fun addUserInFireStore(email: String, userId: String, username: String, fullname: String, profilePic: String) {
        val userData = hashMapOf(
                "fcmToken" to mFCMToken,
                "active" to true,
                "fullName" to fullname,
                "pic" to profilePic,
                "userId" to userId,
                "userName" to username,
                "chatWith" to ""
        )

        FirebaseFirestore.getInstance().collection("AllUsersInfo").document(userId)
                .set(userData)
    }


    private fun getFirebaseToken() {
        if (NetworkUtils.isNetworkConnected(activity!!)) {
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult> {
                        override fun onComplete(task: Task<InstanceIdResult>) {
                            if (!task.isSuccessful) {
                                return
                            }
                            // Get new Instance ID token
                            mFCMToken = task.result!!.token
                            deviceId = Settings.Secure.getString(
                                    context?.contentResolver,
                                    Settings.Secure.ANDROID_ID
                            )
                        }

                    })
        } else {
            baseshowFeedbackMessage(rootLayout,getString(R.string.no_internet))
        }

    }

    fun validateEmailOrPhone(): Boolean {
        var b = false
        if (validations.isValidEmail(editTextEmailOrPhone.text.toString().trim())) {
            phoneOrEmail = editTextEmailOrPhone.text.toString().trim()
            b = true
        } else {
            phoneOrEmail = editTextEmailOrPhone.text.toString().trim()
            val re = Regex("[^0-9]")
            phoneOrEmail = re.replace(phoneOrEmail, "")
            if (validations.isValidMobile(phoneOrEmail)) {
                b = true
            }
        }
        return b
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth!!.currentUser

                when {
                    user!!.email != null -> phoneOrEmail = user.email!!
                    user.providerData[0].email != null -> phoneOrEmail = user.providerData[0].email!!
                    user.providerData[1].email != null -> phoneOrEmail = user.providerData[1].email!!
                }

                if (user?.displayName != null)
                    mUserName = user.displayName!!

                loginViewModel.userIsExist(phoneOrEmail, "")

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", it.exception)
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth!!.currentUser

                when {
                    user!!.email != null -> phoneOrEmail = user.email!!
                    user.providerData[0].email != null -> phoneOrEmail = user.providerData[0].email!!
                    user.providerData[1].email != null -> phoneOrEmail = user.providerData[1].email!!
                }

                if (user?.displayName != null)
                    mUserName = user.displayName!!

                loginViewModel.userIsExist(phoneOrEmail, "")

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", it.exception)
            }

        }

    }

    private fun completeRegistration() {
        loginViewModel.register(
                mUserName,
                phoneOrEmail,
                CommonUtils().getRandomString()!!,
                "",
                "A",
                mFCMToken,
                mFCMToken,
                RegisterFragment.sCountryCode,
                RegisterFragment.sIso,
                Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account!!.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loginViewModel.getUser().removeObservers(activity!!)
    }
}