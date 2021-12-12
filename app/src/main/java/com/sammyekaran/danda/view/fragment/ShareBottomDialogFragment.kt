package com.sammyekaran.danda.view.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.sharePost.SharePostResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.CommonUtils
import kotlinx.android.synthetic.main.view_share_post.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShareBottomDialogFragment(profileId: String, postId: String?,profilePic: String, fullname: String) : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var mDialogProgress: AlertDialog
    val commonUtils: CommonUtils by inject()
    private var profileId = ""
    private var profilePic = ""
    private var fullname = ""
    var postId = ""
    var respositry: WebServices = WebServices()

    init {
        this.profileId = profileId
        this.postId = postId!!
        this.profilePic = profilePic
        this.fullname = fullname
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView: View = LayoutInflater.from(context).inflate(R.layout.view_share_post, container, false)
        if (dialog!!.window != null) {
            dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
        if (activity != null) {
            val decorView = activity!!.window.decorView
            decorView.viewTreeObserver.addOnGlobalLayoutListener {
                val displayFrame = Rect()
                decorView.getWindowVisibleDisplayFrame(displayFrame)
                val height = decorView.context.resources.displayMetrics.heightPixels
                val heightDifference: Int = height - displayFrame.bottom
                if (heightDifference != 0) {
                    if (fragmentView.paddingBottom !== heightDifference) {
                        fragmentView.setPadding(0, 0, 0, heightDifference)
                    }
                } else {
                    if (fragmentView.paddingBottom !== 0) {
                        fragmentView.setPadding(0, 0, 0, 0)
                    }
                }
            }
        }
        dialog!!.setOnShowListener { dialog: DialogInterface ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
                    ?: return@setOnShowListener
            BottomSheetBehavior.from(bottomSheetInternal).setState(STATE_EXPANDED)
        }
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.textViewShare).setOnClickListener(this)
        textViewName.text=fullname

        Glide.with(activity!!).load(profilePic).into(imageViewProfile)
    }

    override fun onClick(view: View) {
        commonUtils.hideKeyboardFrom(activity!!,rootLayout)
        showProgressBar("")
        sharePost(profileId, postId, tvCaption.text.toString())
    }

    private fun sharePost(profileId: String, postId: String, caption: String) {
        respositry.sharePost(profileId,postId, caption).enqueue(object : Callback<SharePostResponse> {
            override fun onFailure(call: Call<SharePostResponse>, t: Throwable) {
                hideProgressDialog()
                Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<SharePostResponse>, response: Response<SharePostResponse>) {
                hideProgressDialog()
                val response= response.body()
                if (response!!.response?.status.equals("0")) {
                    Toast.makeText(activity, response.response.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, response.response.message, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        })
    }

    fun showProgressBar(message: String) {
        val alertDialog = AlertDialog.Builder(activity!!)
        alertDialog.setCancelable(false)
        val view = LayoutInflater.from(activity).inflate(R.layout.view_progress_dialog, null)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = message
        alertDialog.setView(view)
        mDialogProgress = alertDialog.create()
        mDialogProgress.setCancelable(true)
        mDialogProgress.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!mDialogProgress.isShowing) {
            mDialogProgress.show()
        }

        val lp = WindowManager.LayoutParams()
        val window = mDialogProgress.window
        window?.setGravity(Gravity.CENTER)
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
    }

    fun hideProgressDialog() {
        mDialogProgress.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(profileId: String, postId: String?,profilePic:String,fullname:String): ShareBottomDialogFragment {
            return ShareBottomDialogFragment(profileId, postId,profilePic,fullname)
        }
    }
}