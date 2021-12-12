package com.sammyekaran.danda.view.fragment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.model.CountryBeanList
import com.sammyekaran.danda.model.fetchprofile.FetchProfileModel
import com.sammyekaran.danda.model.updateprofile.Data
import com.sammyekaran.danda.model.updateprofile.UpdateProfileModel
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.utils.Validations
import com.sammyekaran.danda.view.activity.VerifyPhoneActivity
import com.sammyekaran.danda.view.adapter.CountryListAdapter
import com.sammyekaran.danda.viewmodel.EditViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.imageButtonBack
import kotlinx.android.synthetic.main.view_change_photo.view.*
import kotlinx.android.synthetic.main.view_gender.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.*


class EditProfileFragment : BaseFragmentWithoutBinding() {

    val REQUEST_CODE_CAMERA = 1001
    val REQUEST_CODE_GALLERY = 1002
    val STORAGE_PERMISSION_CODE = 10003
    private val VERIFICATION_REQUEST_CODE = 1004

    val viewModel: EditViewModel by viewModel()
    val sharedPref: SharedPref by inject()
    val commonUtils: CommonUtils by inject()
    internal var fileUserImage: File? = null
    private var mProfileUrl = "null"
    private var mUserImage: String? = null
    var mParms = HashMap<String, RequestBody>()
    var profilePic: MultipartBody.Part? = null
    var requestFile1: RequestBody? = null
    var mGender = "1"
    lateinit var countryJsonString: String

    var mProfile = FetchProfileModel()
    var filterList: MutableList<CountryBeanList.Datum> = mutableListOf()
    lateinit var listAdapter: CountryListAdapter
    lateinit var countryList: List<CountryBeanList.Datum>
    lateinit var countryBeanList: CountryBeanList
    var countrtDialog: Dialog? = null

    var mCountryCode = ""
    var mIso = ""
    var recyclerView: RecyclerView? = null

    private val permission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )


    companion object {
        var isApiHit = false

    }

    private val validations = Validations()


    override fun getLayoutId(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tm = activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso
        if (countryCodeValue == "ken"){
            mIso = "KEN"
            mCountryCode ="254"
        }else{
            mIso = "KEN"
            mCountryCode ="254"
        }
        initObserver()
        viewModel.fetchProfile(sharedPref.getString(Constants.USER_ID))
        countryJsonString = readJSONFromAsset()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listner()

    }

    private fun listner() {
        textViewChangePhoto.setOnClickListener {

            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        STORAGE_PERMISSION_CODE
                )
            } else {
                changePhotoDialog()
            }
        }

        imageViewSave.setOnClickListener { updateProfile() }
        textViewGender.setOnClickListener { selectGenderDialog() }
        imageButtonBack.setOnClickListener { findNavController().popBackStack() }
        textViewCountry.setOnClickListener { showCountryDialog() }
    }

    private fun updateProfile() {
        commonUtils.hideSoftKeyBoard(activity!!)
        var phoneNumber = editTextPhoneNumber.text.toString()
        val re = Regex("[^0-9]")
        phoneNumber = re.replace(phoneNumber, "")

        if (validations.isEmpty(editTextName.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter NAME.")
        }/* else if (!validations.isValid(editTextName.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter valid name.")
        } */else if (validations.isEmpty(editTextUserName.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter Username.")
        } else if (validations.isEmpty(editTextEmail.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter E-mail Id.")
        } else if (!validations.isValidEmail(editTextEmail.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter valid E-mail Id.")
        } else if (validations.isEmpty(mCountryCode)) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please select country.")
        } else if (validations.isEmpty(editTextPhoneNumber.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter phone number.")
        } else if (!validations.isValidMobile(phoneNumber.trim())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please enter valid phone number.")
        } else if (validations.isEmpty(textViewGender.text.toString())) {
            baseshowFeedbackMessage(rootLayoutEditProfile, "Please select gender.")
        } else {

            if (mProfile.response?.data?.contact.isNullOrEmpty()&&phoneNumber.trim().isNotEmpty()) {
                viewModel.userIsExist("", phoneNumber)
            } else if ((mProfile.response?.data?.countryCode + re.replace(mProfile.response?.data?.contact!!, "")) != (mCountryCode + phoneNumber.trim())) {
                viewModel.userIsExist("", phoneNumber)
            } else {
                updateProfileRequest()
            }


        }
    }

    private fun updateProfileRequest() {
        var phoneNumber = editTextPhoneNumber.text.toString()
        val re = Regex("[^0-9]")
        phoneNumber = re.replace(phoneNumber, "")
        if (mUserImage == null) {
            requestFile1 = RequestBody.create(MediaType.parse("*//*"), fileUserImage)
            profilePic = MultipartBody.Part.createFormData("profile_pic", fileUserImage!!.name, requestFile1)
        } else {
            val attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "")
            profilePic = MultipartBody.Part.createFormData("profile_pic", "", attachmentEmpty)
        }

        mParms["username"] = commonUtils.toRequestBody(editTextUserName.text.toString())
        mParms["user_id"] = commonUtils.toRequestBody(sharedPref.getString(Constants.USER_ID))
        mParms["name"] = commonUtils.toRequestBody(editTextName.text.toString())
        mParms["email"] = commonUtils.toRequestBody(editTextEmail.text.toString())
        mParms["contact"] = commonUtils.toRequestBody(phoneNumber)
        mParms["country_code"] = commonUtils.toRequestBody(mCountryCode)
        mParms["countryIso"] = commonUtils.toRequestBody(mIso)
        mParms["gender"] = commonUtils.toRequestBody(mGender)
        mParms["bio"] = commonUtils.toRequestBody(editTextBio.text.toString())
        viewModel.updateProfiles(mParms, profilePic!!)

    }


    private fun initObserver() {
        viewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
            if (isLoading) {
                baseShowProgressBar("")
            } else {
                baseHideProgressDialog()
            }
        })

        viewModel.updateProfile?.observe(this, Observer<UpdateProfileModel> { updateProfileModel ->
            if (updateProfileModel.response?.status.equals("0")) {
                baseshowFeedbackMessage(rootLayoutEditProfile, updateProfileModel.response?.message!!)
            } else {
                if (editTextEmail.text.toString().trim() != mProfile.response?.data?.email?.trim()) {
                    updateFirebaseSignInEmail(updateProfileModel.response?.data)
                } else {
                    updateUserInFireStore(updateProfileModel.response?.data)
                    sharedPref.setString(Constants.FULL_NAME, editTextName.text.toString())
                    findNavController().popBackStack()
                }

            }
        })

        viewModel.fetchProfile?.observe(this, Observer<FetchProfileModel> { profile ->
            isApiHit = true
            mProfile = profile
            if (profile.response?.status.equals("0")) {
                baseshowFeedbackMessage(rootLayoutEditProfile, profile.response?.message!!)
            } else {
                mCountryCode = mProfile.response?.data?.countryCode!!
                if (!mProfile.response?.data?.countryCode!!.isEmpty())
                    textViewCountry.text = mProfile.response?.data?.countryIso + "+" + mProfile.response?.data?.countryCode?.replace("+", "")
                if (profile.response?.data?.gender.equals("1", true)) {
                    textViewGender.text = "Male"
                } else {
                    textViewGender.text = "Female"
                }
                editTextUserName.setText(profile.response?.data?.username)
                editTextName.setText(profile.response?.data?.fullname)
                editTextBio.setText(profile.response?.data?.bio)
                editTextEmail.setText(profile.response?.data?.email)
                editTextPhoneNumber.setText(profile.response?.data?.contact)


                if (!profile.response?.data?.profilePic!!.isEmpty()) {
                    val url = URL(Constants.BASE_URL)
                    fileUserImage = File(url.file)
                    Glide.with(activity!!)
                            .load(profile?.response?.data?.profilePic)
                            .into(imageViewUser)
                }
                mUserImage = ""

            }
        })

        viewModel.errorListener!!.observe(this, Observer<String> { message -> baseshowFeedbackMessage(rootLayoutEditProfile, message!!) })

        viewModel.isUserExist!!.observe(this, Observer { it ->
            if (it?.response?.status.equals("1")) {
                verifyPhoneNumber()
            } else {
                baseshowFeedbackMessage(rootLayoutEditProfile, it?.response?.message!!)
            }
        })
    }

    private fun updateFirebaseSignInEmail(data: Data?) {
        baseShowProgressBar("Wait...")
        val user = FirebaseAuth.getInstance().getCurrentUser()
        // Get auth credentials from the user for re-authentication
        val credential = EmailAuthProvider
                .getCredential(mProfile.response?.data?.email!!, mProfile.response?.data?.password!!) // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user?.reauthenticateAndRetrieveData(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                baseHideProgressDialog()
                user.updateEmail(editTextEmail.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("EditProfile", "Email updated")
                                sharedPref.setString(Constants.EMAIL, editTextEmail.text.toString())
                                updateUserInFireStore(data)
                                sharedPref.setString(Constants.FULL_NAME, editTextName.text.toString())
                                findNavController().popBackStack()
                            } else {
                                Log.d("EditProfile", "Error Email not updated")
                            }
                        }
            } else {
                baseHideProgressDialog()
                Log.d("EditProfile", "Error auth failed")
            }

        }
    }

    private fun verifyPhoneNumber() {
        var phoneNumber = editTextPhoneNumber.text.toString()
        val re = Regex("[^0-9]")
        phoneNumber = re.replace(phoneNumber, "")
        startActivityForResult(Intent(activity, VerifyPhoneActivity::class.java)
                .putExtra("countryCode", mCountryCode)
                .putExtra("mobile", phoneNumber), VERIFICATION_REQUEST_CODE)
    }


    private fun selectGenderDialog() {

        val layoutInflater = activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(com.sammyekaran.danda.R.layout.view_gender, null)
        val dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        dialog.window!!.findViewById<View>(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent)
        view.textViewCancel.setOnClickListener({ dialog.dismiss() })
        view.textViewMale.setOnClickListener {
            textViewGender.text = "Male"
            mGender = "1"
            dialog.dismiss()
        }
        view.textVieWFemale.setOnClickListener {
            textViewGender.text = "Female"
            mGender = "2"
            dialog.dismiss()
        }
        dialog.show()
        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }

    private fun changePhotoDialog() {

        val layoutInflater = activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_change_photo, null)
        val dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        dialog.window!!.findViewById<View>(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent)
        view.textViewDismiss.setOnClickListener({ dialog.dismiss() })
        view.textViewGallery.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }
        view.textViewCamera.setOnClickListener {
            dialog.dismiss()
            openCamera()
        }
        dialog.show()
        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }

    private fun openCamera() {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAMERA)

    }


    private fun openGallery() {
        startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                REQUEST_CODE_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    CropImage.activity(data?.data).setCropMenuCropButtonTitle("Ok").start(context!!, this)
                }

                REQUEST_CODE_GALLERY -> {
                    CropImage.activity(data?.data).setCropMenuCropButtonTitle("Ok").start(context!!, this)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                    if (resultCode == Activity.RESULT_OK) {
                        mUserImage = null

                        fileUserImage = File(CropImage.getActivityResult(data).uri.path)
                        val bitmap = BitmapFactory.decodeFile(fileUserImage.toString())
                        fileUserImage = bitmapToFile(context!!, bitmap)

                        Glide.with(activity!!)
                                .load(fileUserImage)
                                .error(R.drawable.ic_icon_avatar)
                                .into(imageViewUser)
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    }
                }

                VERIFICATION_REQUEST_CODE -> {
                    updateProfileRequest()
                }


            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            changePhotoDialog()
        }
    }

    private fun updateUserInFireStore(data: Data?) {
        FirebaseFirestore.getInstance().collection("AllUsersInfo").document(sharedPref.getString(Constants.USER_ID))
                .update(mapOf(
                        "fullName" to data?.fullname,
                        "pic" to data?.profilePic,
                        "userName" to data?.username
                ))
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun showCountryDialog() {
        countrtDialog = Dialog(activity!!)
        countrtDialog!!.setContentView(R.layout.dialog_country)
        countrtDialog!!.setTitle("Select country code")
        recyclerView = countrtDialog!!.findViewById<RecyclerView>(R.id.recyclerView)
        val editTextSearch = countrtDialog!!.findViewById<EditText>(R.id.editTextSearch)
        countrtDialog!!.findViewById<TextView>(R.id.textViewCancel).setOnClickListener {
            countrtDialog?.cancel()
        }
        editTextSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        setCountryAdapter()
        countrtDialog!!.show()
    }

    private fun setCountryAdapter() {

        var jsonObject: JSONObject? = null
        try {
            val jsonArray = JSONArray(countryJsonString)
            jsonObject = JSONObject()
            jsonObject.put("data", jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        countryBeanList = Gson().fromJson(jsonObject!!.toString(), CountryBeanList::class.java)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        countryList = countryBeanList.data!!
        listAdapter = CountryListAdapter(countryList, object : CountryListAdapter.ItemClick {
            override fun onItemClick(countryName: String, countryCode: String, iso: String) {
                commonUtils.hideSoftKeyBoard(activity!!)
                mIso = iso
                mCountryCode = countryCode.replace("+", "")
                textViewCountry.text = "$mIso + $mCountryCode"
                countrtDialog?.cancel()
            }

        })
        recyclerView?.adapter = listAdapter

    }

    private fun readJSONFromAsset(): String {
        var json = ""
        try {
            val inputStream: InputStream = resources.openRawResource(com.sammyekaran.danda.R.raw.countrycodes)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    fun filter(text: String) {
        filterList = ArrayList()
        for (d in countryList) { //or use .equal(text) with you want equal match //use .toLowerCase() for better matches
            if (d.name!!.toLowerCase().contains(text.toLowerCase().trim())) {
                filterList.add(d)
            }
        }
        //update recyclerview
        listAdapter.updateList(filterList)
    }


}