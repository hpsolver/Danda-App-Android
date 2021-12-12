package com.sammyekaran.danda.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.internal.view.SupportMenu
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar
import com.sammyekaran.danda.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    var mDialogProgress: AlertDialog? = null
    open lateinit var mViewDataBinding: T
    var permissionNeeded = 0


    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    fun getViewDataBinding(): T {
        return mViewDataBinding
    }

    fun init(inflater: LayoutInflater, container: ViewGroup) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
    }

    open fun init() {}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        init(inflater, container!!)
        init()
        super.onCreateView(inflater, container, savedInstanceState)
        return mViewDataBinding.root
    }

    fun checkPermission(permissions: Array<String>): Int {
        permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permissions.indices) {
                val result = ContextCompat.checkSelfPermission(activity!!, permissions[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }

    fun getPath(uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context?.contentResolver?.query(uri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            Log.e("HOME_FRAGMENT", "getRealPathFromURI Exception : $e")
            return ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    @SuppressLint("ResourceType")
    fun baseShowProgressBar(message: String) {
        /* val alertDialog = AlertDialog.Builder(activity!!)
         alertDialog.setCancelable(false)
         val view = LayoutInflater.from(activity).inflate(com.sammyekaran.danda.R.layout.view_progress_dialog, null)
         val textView = view.findViewById<TextView>(com.sammyekaran.danda.R.id.textView)
         textView.text = message
         alertDialog.setView(view)
         mDialogProgress = alertDialog.create()
         mDialogProgress.setCancelable(true)
         mDialogProgress.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         if (!mDialogProgress.isShowing) {
             mDialogProgress.show()
         }

         val lp = WindowManager.LayoutParams()
         val window = mDialogProgress.window
         window.setGravity(Gravity.CENTER)
         lp.copyFrom(window.attributes)
         lp.width = WindowManager.LayoutParams.MATCH_PARENT
         lp.height = WindowManager.LayoutParams.MATCH_PARENT
         window.attributes = lp*/

        if (mDialogProgress == null) {
            val alertDialog = AlertDialog.Builder(activity!!, resources.getColor(R.color.transparent))
            val view = LayoutInflater.from(activity!!).inflate(R.layout.custom_progress_bar, null)
            alertDialog.setView(view)
            val circleProgressBar: CircleProgressBar = view.findViewById(R.id.loader)
            circleProgressBar.setColorSchemeColors(SupportMenu.CATEGORY_MASK, ViewCompat.MEASURED_STATE_MASK)
            mDialogProgress = alertDialog.create()
            mDialogProgress?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
            val lp = WindowManager.LayoutParams()
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            mDialogProgress?.window?.attributes = lp
            mDialogProgress?.setCancelable(true)
        }
        if (mDialogProgress != null && !mDialogProgress!!.isShowing) {
            mDialogProgress?.show()
        }

    }

    fun baseHideProgressDialog() {
        mDialogProgress?.dismiss()
    }

    fun baseshowFeedbackMessage(view: View, message: String) {
        val snakbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snakbar.view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        if (snakbar.isShown) {
            snakbar.dismiss()
        }
        snakbar.show()
    }

    //method to get the file path from uri
    fun getImagePath(uri: Uri, context: Context): String {
        var cursor = context.getContentResolver().query(uri, null, null, null, null)
        cursor?.moveToFirst()
        var document_id = cursor?.getString(0)
        document_id = document_id?.substring(document_id.lastIndexOf(":") + 1)
        cursor?.close()

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID + " = ? ",
                arrayOf<String>(document_id!!),
                null
        )
        cursor?.moveToFirst()
        val path = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor?.close()

        return path!!
    }

    //method to get the file path from bitmap
    fun bitmapToFile(context: Context, bitmap: Bitmap?): File {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "profile" + ".jpg")

        if (bitmap == null)
            return imageFile

        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e("Utils", "Error writing bitmap", e)
        }

        return imageFile
    }

    //Get FCM Token
    fun fcmToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult> {
                    override fun onComplete(task: Task<InstanceIdResult>) {
                        if (!task.isSuccessful()) {
                            Log.w("REGISTER", "getInstanceId failed", task.getException())
                            return
                        }
                        // Get new Instance ID token
                        // token = task.getResult()?.getToken()!!
                    }
                })
    }

}