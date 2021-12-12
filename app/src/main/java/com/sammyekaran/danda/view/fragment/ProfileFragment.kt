package com.sammyekaran.danda.view.fragment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentProfileBinding
import com.sammyekaran.danda.model.profile.Post
import com.sammyekaran.danda.model.profile.ProfileModel
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.GridSpacingItemDecoration
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.PostsAdapter
import com.sammyekaran.danda.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.block_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.adView
import kotlinx.android.synthetic.main.popup_profile_layout.view.*
import kotlinx.android.synthetic.main.unfollow_dialog.view.*
import kotlinx.android.synthetic.main.unfollow_dialog.view.tvCancel
import kotlinx.android.synthetic.main.view_change_photo.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*
import kotlin.math.roundToInt


class ProfileFragment : BaseFragment<FragmentProfileBinding>(){


    val REQUEST_CODE_CAMERA = 101
    val REQUEST_CODE_GALLERY = 102
    val STORAGE_PERMISSION_CODE = 103

    private lateinit var mAdapter: PostsAdapter
    val mViewModel: ProfileViewModel by viewModel()
    lateinit var binding: FragmentProfileBinding
    var mUserId = ""
    var mUserTypeString = ""
    var mUserBlockStatus = ""
    var fileUserImage: File? = null
    var mParms = HashMap<String, RequestBody>()
    var profilePic: MultipartBody.Part? = null
    var requestFile: RequestBody? = null
    val utils: CommonUtils by inject()
    val sharedPref: SharedPref by inject()
    lateinit var profileModel: ProfileModel
    val mPostList = listOf<Post>()
    val commonUtils: CommonUtils by inject()
    var isSubscribed=false


    val permission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )


    companion object {
        var sTotalFolloers = ""
        var sTotalFollowings = ""
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUpObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = mViewModel


        if (sharedPref.getString(Constants.USER_ID).isEmpty()) {
            val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        loadBannerAd()
    }

    private fun loadBannerAd() {
        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

        val testDeviceIds: List<String> = Arrays.asList("2E7CF230E53C9FA42F77B8A611DBB422")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        adView.loadAd(AdRequest.Builder().build())
    }


    private fun listener() {
        imageViewUpdate.setOnClickListener {
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

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args: ProfileFragmentArgs by navArgs()
        if (args.userId.equals("0")) {
            mUserId = sharedPref.getString(Constants.USER_ID)
        } else {
            mUserId = args.userId
        }
        var fromUserId: String
        if (mUserId == sharedPref.getString(Constants.USER_ID)) {
            fromUserId = ""
        } else {
            fromUserId = sharedPref.getString(Constants.USER_ID)
        }
        mViewModel.getProfile(mUserId, fromUserId, activity!!)

        listener()

        imageViewOption.setOnClickListener {
            showOptionPopUp(imageViewOption)
        }
        textViewUnblock.setOnClickListener {
            mViewModel.blockUnBlock(mUserId, sharedPref.getString(Constants.USER_ID), "0")
        }
        textViewFollowUnFollow.setOnClickListener {
            //1=follow,2=unfollow
            if (mUserTypeString.equals("Follow")) {
                mViewModel.followUnfollow(mUserId, sharedPref.getString(Constants.USER_ID), "1")
            } else if (mUserTypeString.equals("UnFollow")) {
                showUnfollowDialog()
            }
        }


    }




    private fun setAdapter(data: ProfileModel?) {
        recyclerView?.layoutManager = GridLayoutManager(activity!!, 3)
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(3, dpToPx(4), true))
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.isNestedScrollingEnabled = false
        mAdapter = PostsAdapter(
                data?.response?.data?.posts, mViewModel,
                sharedPref.getString(Constants.USER_ID), mUserId, isSubscribed
        )
        recyclerView?.adapter = mAdapter
    }

    fun initUpObserver() {
        mViewModel.profileData!!.observe(this, Observer<ProfileModel> { data ->
            profileModel = data!!
            if (data.response?.status.equals("0")) {
                baseshowFeedbackMessage(rootLayoutProfile, data.response?.message!!)
            } else {
                binding.profile = profileModel
                sTotalFolloers = data.response?.data?.followersCount.toString()
                sTotalFollowings = data.response?.data?.followingCount.toString()

                if (data.response?.data?.usersTypes.equals("0")) {
                    mUserTypeString = "Follow"
                    textViewFollowUnFollow.text = mUserTypeString
                } else if (data.response?.data?.usersTypes.equals("1")) {
                    mUserTypeString = "UnFollow"
                    textViewFollowUnFollow.text = mUserTypeString
                }

                if (data.response?.data?.isBlock.equals("0")) {
                    mUserBlockStatus = "Block"
                    textViewUnblock.text = mUserBlockStatus
                } else if (data.response?.data?.isBlock.equals("1")) {
                    mUserBlockStatus = "UnBlock"
                    textViewUnblock.text = mUserBlockStatus
                }


                when {
                    sharedPref.getString(Constants.USER_ID) == data.response?.data?.userId -> {
                        textViewEdit.visibility = View.VISIBLE
                        imageViewSetting.visibility = View.VISIBLE
                        imageViewOption.visibility = View.GONE
                        imageViewBack.visibility = View.GONE
                        linearLayoutMessage.visibility = View.GONE
                        imageViewUpdate.visibility = View.VISIBLE
                        textViewUnblock.visibility = View.GONE
                    }
                    data.response?.data?.isBlock.equals("0") -> {
                        imageViewSetting.visibility = View.GONE
                        imageViewBack.visibility = View.VISIBLE
                        imageViewOption.visibility = View.VISIBLE
                        imageViewUpdate.visibility = View.GONE
                        textViewEdit.visibility = View.GONE
                       linearLayoutMessage.visibility = View.VISIBLE
                        textViewUnblock.visibility = View.GONE
                    }
                    data.response?.data?.isBlock.equals("1") -> {
                        imageViewSetting.visibility = View.GONE
                        imageViewBack.visibility = View.VISIBLE
                        imageViewUpdate.visibility = View.GONE
                        imageViewOption.visibility = View.VISIBLE
                        textViewEdit.visibility = View.GONE
                        linearLayoutMessage.visibility = View.GONE
                        textViewUnblock.visibility = View.VISIBLE
                    }
                }
                if (data.response?.data?.posts?.size == 0) {
                    recyclerView.visibility = View.GONE
                    tvMessage.visibility = View.VISIBLE
                    if (sharedPref.getString(Constants.USER_ID).equals(data.response?.data?.userId)) {
                        tvMessage.text = getString(R.string.upload_images)
                    } else {
                        tvMessage.text = getString(R.string.error_no_images)
                    }
                } else {
                    recyclerView.visibility = View.VISIBLE
                    tvMessage.visibility = View.GONE
                }
                setAdapter(data)
            }
        })




        mViewModel.errorListener!!.observe(this, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayoutProfile, message!!)
            }
        })

        mViewModel.followUnfollow!!.observe(this, Observer { followUnfollow ->
            if (followUnfollow.response?.status.equals("0")) {
                baseshowFeedbackMessage(rootLayoutProfile, followUnfollow.response?.message!!)
            } else {
                if (mUserTypeString.equals("Follow")) {
                    mUserTypeString = "UnFollow"
                    profileModel.response?.data?.usersTypes = "1"
                } else if (mUserTypeString.equals("UnFollow")) {
                    mUserTypeString = "Follow"
                    profileModel.response?.data?.usersTypes = "0"
                }
                textViewFollowUnFollow.text = mUserTypeString
            }
        })


        mViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                baseShowProgressBar("")
            } else {
                baseHideProgressDialog()
            }
        })

        mViewModel.blockUnblock?.observe(this, Observer { it ->
            if (it.response?.status.equals("1")) {
                if (mUserBlockStatus.equals("Block")) {
                    mUserBlockStatus = "UnBlock"
                    textViewEdit.visibility = View.GONE
                   linearLayoutMessage.visibility = View.GONE
                    textViewUnblock.visibility = View.VISIBLE
                    textViewUnblock.text = mUserBlockStatus
                    mAdapter.customNotify(mPostList)
                } else {
                    mUserBlockStatus = "Block"
                    linearLayoutMessage.visibility = View.VISIBLE
                    textViewUnblock.visibility = View.GONE
                    textViewUnblock.text = mUserBlockStatus
                    mUserTypeString = "Follow"
                    textViewFollowUnFollow.text = mUserTypeString
                    profileModel.response?.data?.usersTypes = "0"
                }


            } else {
                baseshowFeedbackMessage(rootLayoutProfile, "Something went wrong.")
            }
        })

        mViewModel.showFeedbackMessage!!.observe(this, Observer { message -> baseshowFeedbackMessage(rootLayoutProfile, message) })


        mViewModel.updateProfilePicResponse?.observe(this, Observer { data ->
            if (data.response?.status.equals("1")) {
                FirebaseFirestore.getInstance().collection("AllUsersInfo")
                        .document(sharedPref.getString(Constants.USER_ID))
                        .update("pic", data.response?.profile_pic)
                        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.d("TAG", "Error writing document", e) }
            } else {
                baseshowFeedbackMessage(rootLayoutProfile, data.response?.message!!)
            }
        })
    }

    /**
     * Converting dp to pixel
     */
    private fun dpToPx(dp: Int): Int {
        val r = resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).roundToInt()
    }


    fun changePhotoDialog() {
        val layoutInflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_change_photo, null)
        val dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(view)
        dialog.window!!.findViewById<View>(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent)
        view.textViewDismiss.setOnClickListener { dialog.dismiss() }
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
        if (resultCode != Activity.RESULT_CANCELED) {

            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    CropImage.activity(data?.data).setCropMenuCropButtonTitle("Ok").start(context!!, this)
                }

                REQUEST_CODE_GALLERY -> {
                    CropImage.activity(data?.data).setCropMenuCropButtonTitle("Ok").start(context!!, this)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                    if (resultCode == RESULT_OK) {

                        fileUserImage = File(CropImage.getActivityResult(data).uri.path)
                        val bitmap = BitmapFactory.decodeFile(fileUserImage.toString())
                        fileUserImage = bitmapToFile(context!!, bitmap)

                        Glide.with(activity!!)
                                .load(fileUserImage)
                                .error(R.drawable.ic_icon_avatar)
                                .into(imageViewProfile)

                        updateProfilePhoto()
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    }
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

    fun updateProfilePhoto() {
        requestFile = RequestBody.create(MediaType.parse("*//*"), fileUserImage!!)
        profilePic = MultipartBody.Part.createFormData("profile_pic", fileUserImage!!.name, requestFile!!)

        mParms["user_id"] = utils.toRequestBody(sharedPref.getString(Constants.USER_ID))
        mViewModel.updateProfilePic(mParms, profilePic!!)
    }

    fun showOptionPopUp(view: View) {

        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_profile_layout, null)
        dialogBuilder.setView(dialogView)

        val b = dialogBuilder.create()
        b.show()

        dialogView.textViewBlock.text = mUserBlockStatus
        dialogView.textViewFollow.text = mUserTypeString

        dialogView.textViewCopyUrl.setOnClickListener {
            val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val copyUri: Uri = Uri.parse(profileModel.response?.data?.copyUrl)
            val clip = ClipData.newUri(activity?.contentResolver, "URI", copyUri)
            clipboard.primaryClip?.addItem(clip.getItemAt(0))
            b.dismiss()
            Toast.makeText(context, "URL Copied !!", Toast.LENGTH_SHORT).show()
        }
        dialogView.textViewBlock.setOnClickListener {
            b.dismiss()
            if (mUserBlockStatus.equals("Block")) {
                showBlockDialog()
            } else {
                mViewModel.blockUnBlock(mUserId, sharedPref.getString(Constants.USER_ID), "0")
            }

        }
        dialogView.textViewFollow.setOnClickListener {
            b.dismiss()
            textViewFollowUnFollow.performClick()
        }

    }


    fun showUnfollowDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.unfollow_dialog, null)
        dialogBuilder.setView(dialogView)

        val b = dialogBuilder.create()
        b.show()

        Glide.with(activity!!)
                .load(profileModel.response?.data?.profilePic)
                .error(R.drawable.ic_icon_avatar)
                .into(dialogView.imageViewProfile)

        var message = "Unfollow " + "<b>" + "@" + textViewUserName.text.toString() + "</b> " + "?"
        dialogView.tvMessage.text = Html.fromHtml(message)
        dialogView.tvCancel.setOnClickListener { b.dismiss() }
        dialogView.tvUnfollow.setOnClickListener {
            b.dismiss()
            mViewModel.followUnfollow(mUserId, sharedPref.getString(Constants.USER_ID), "2")
        }
    }


    private fun showBlockDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.block_dialog, null)
        dialogBuilder.setView(dialogView)

        val b = dialogBuilder.create()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(b.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        b.show()
        b.window?.attributes = lp

        val message = "Block " + "<b>" + "@" + textViewUserName.text.toString() + "</b> " + "?"
        dialogView.imageViewProfile
        dialogView.textViewName.text = Html.fromHtml(message)
        dialogView.tvBlock.setOnClickListener {
            b.dismiss()
            b.dismiss()
            mViewModel.blockUnBlock(mUserId, sharedPref.getString(Constants.USER_ID), "1")
        }
        dialogView.tvBlockCancel.setOnClickListener { b.dismiss() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //mViewModel.followUnfollow?.removeObservers(this)
        //mViewModel.profileData?.removeObservers(this)
        //mViewModel.errorListener?.removeObservers(this)
    }


}