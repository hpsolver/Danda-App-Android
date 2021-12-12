package com.sammyekaran.danda.base

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.internal.view.SupportMenu
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar
import com.sammyekaran.danda.R

abstract class BaseActivity : AppCompatActivity() {

    var mDialogProgress: AlertDialog? = null
    @SuppressLint("ResourceType")
    fun baseShowProgressBar(message: String) {
        /* val alertDialog = AlertDialog.Builder(this)
         alertDialog.setCancelable(false)
         val view = LayoutInflater.from(this).inflate(R.layout.view_progress_dialog, null)
         val textView = view.findViewById<TextView>(R.id.textView)
         textView.text = message
         alertDialog.setView(view)
         mDialogProgress = alertDialog.create()
         mDialogProgress!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         if (!mDialogProgress!!.isShowing) {
             mDialogProgress!!.show()
         }

         val lp = WindowManager.LayoutParams()
         val window = mDialogProgress!!.window
         window.setGravity(Gravity.CENTER)
         lp.copyFrom(window.attributes)
         lp.width = WindowManager.LayoutParams.MATCH_PARENT
         lp.height = WindowManager.LayoutParams.MATCH_PARENT
         window.attributes = lp
 */

        if (mDialogProgress == null) {
            val alertDialog = AlertDialog.Builder(this, resources.getColor(R.color.transparent))
            val view = LayoutInflater.from(this).inflate(R.layout.custom_progress_bar, null)
            alertDialog.setView(view)
            val circleProgressBar: CircleProgressBar = view.findViewById(R.id.loader)
            circleProgressBar.setColorSchemeColors(SupportMenu.CATEGORY_MASK, ViewCompat.MEASURED_STATE_MASK)
            mDialogProgress = alertDialog.create()
            mDialogProgress?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
            val lp = WindowManager.LayoutParams()
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            mDialogProgress?.window?.attributes = lp
            mDialogProgress!!.setCancelable(true)
        }
        if (mDialogProgress != null && !mDialogProgress!!.isShowing) {
            mDialogProgress!!.show()
        }

    }

    fun baseHideProgressDialog() {
        if (mDialogProgress != null && mDialogProgress!!.isShowing) {
            mDialogProgress!!.dismiss()
        }
    }

    fun baseshowFeedbackMessage(view: View, message: String) {
        val snakbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snakbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        if (snakbar.isShown) {
            snakbar.dismiss()
        }
        snakbar.show()
    }

    fun showError(error: String) {
        Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show()
    }

    fun showApiError(responseString: String) {
        showError(responseString)
    }

    fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path.toString()
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}