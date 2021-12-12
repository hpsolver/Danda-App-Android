package com.sammyekaran.danda.view.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentUploadFeedBinding
import com.sammyekaran.danda.model.ProductLinkBean
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.model.uploadPost.UploadPostModel
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.activity.StartActivity
import com.sammyekaran.danda.view.adapter.SuggestionAdapter
import com.sammyekaran.danda.viewmodel.UploadFeedViewModel
import com.vincent.videocompressor.VideoCompress
import kotlinx.android.synthetic.main.fragment_upload_feed.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class UploadFeedFragment : BaseFragment<FragmentUploadFeedBinding>() {

    lateinit var binding: FragmentUploadFeedBinding
    val viewModel: UploadFeedViewModel by viewModel()
    var origanPath = ""
    var mParms = HashMap<String, RequestBody>()
    var post: MultipartBody.Part? = null
    var requestFile: RequestBody? = null
    lateinit var filePost: File
    val utils: CommonUtils by inject()
    val sharedPref: SharedPref by inject()
    var uploadType = ""
    var thumbnailBase64 = ""
    private val commonUtils: CommonUtils by inject()


    companion object {
        var data: MutableList<ProductLinkBean> = mutableListOf()
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_upload_feed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel

        val args: UploadFeedFragmentArgs by navArgs()
        origanPath = args.path
        uploadType = args.type


        if (uploadType == "I") {
            imageViewPost.visibility = View.VISIBLE
            frameLayoutVideo.visibility = View.GONE

            Glide.with(imageViewPost)
                    .asBitmap()
                    .load(origanPath)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            imageViewPost.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })


        } else if (uploadType == "G") {
            imageViewPost.visibility = View.VISIBLE
            frameLayoutVideo.visibility = View.GONE

            Glide.with(imageViewPost)
                    .load(origanPath)
                    .into(imageViewPost)
        } else {
            imageViewPost.visibility = View.GONE
            frameLayoutVideo.visibility = View.VISIBLE
            videoView.setVideoPath(origanPath)
        }

        listener()
        videoListner()

        editTextTitleChangeListener()
        initObserver()

    }

    private fun editTextTitleChangeListener() {
        editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("After Text", p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Before Text", p0.toString() + " " + p1 + " " + p2 + " " + p3)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("On Text", p0.toString() + " " + p1 + " " + p2 + " " + p3)
                parseText(p0.toString())
            }

        })
    }

    private fun parseText(text: String) {
        val words = text.split("\\s".toRegex())
        if (words[words.size - 1].trim().isNotEmpty() && words[words.size - 1].trim().length > 1 && words[words.size - 1].trim().take(1) == "@") {
            println(words[words.size - 1].substring(1))
            viewModel.tagSuggestion(words[words.size - 1].substring(1))
        }


    }

    private fun videoListner() {
        videoView.setOnCompletionListener {
            imageButtonPlay.visibility = View.VISIBLE
        }
    }

    private fun listener() {

        buttonUploadFeed.setOnClickListener { uploadFeed() }
        imageButtonPlay.setOnClickListener {
            imageButtonPlay.visibility = View.GONE
            videoView.start()
        }

    }

    private fun uploadFeed() {
        commonUtils.hideSoftKeyBoard(activity!!)


        filePost = File(origanPath)

        if (uploadType.equals("V", true))
            compressVideo(Uri.parse(origanPath).path.toString())
        else {
            uploadToServer()
        }


    }

    fun compressVideo(inputPath: String) {

        val myDir = File(activity!!.cacheDir, Constants.APPNAME)
        if (!myDir.exists())
            myDir.mkdir()

        val destPath = myDir.toString() + File.separator + "VID_" + SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(Date()) + ".mp4"

        VideoCompress.compressVideoMedium(inputPath, destPath, object : VideoCompress.CompressListener {

            override fun onStart() {
                baseShowProgressBar("Compressing...")
            }

            override fun onProgress(percent: Float) {

            }

            override fun onSuccess() {
                baseHideProgressDialog()
                //File
                filePost = File(destPath)
                Log.d("FileSize after compress", filePost.length().toString())
                uploadToServer()
            }

            override fun onFail() {
                baseHideProgressDialog()
            }

        })


    }

    private fun uploadToServer() {
        commonUtils.hideSoftKeyBoard(activity!!)

        var tagUser = ""
        if (editTextTitle.text!!.contains(" ")) {
            val words = editTextTitle.text?.split("\\s".toRegex())!!.toMutableList()

            for (item in words) {
                if (item.startsWith("@")) {
                    tagUser += item.substring(1) + ","
                }
            }
            if (tagUser.isNotEmpty()) {
                tagUser = tagUser.substring(tagUser.length - 1)
            }
        } else {
            if (editTextTitle.text!!.startsWith("@")) {
                tagUser += editTextTitle.text
            }
        }



        requestFile = RequestBody.create(MediaType.parse("*//*"), filePost)
        post = MultipartBody.Part.createFormData("posts", filePost.name, requestFile)

        if (uploadType.equals("v", true)) {
            val thumb = ThumbnailUtils.createVideoThumbnail(origanPath, MediaStore.Images.Thumbnails.MINI_KIND)
            thumbnailBase64 = getBase64(thumb!!)
        } else  if (uploadType.equals("i", true)) {
            val thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(origanPath), MediaStore.Images.Thumbnails.MINI_KIND, MediaStore.Images.Thumbnails.MINI_KIND)
            thumbnailBase64 = getBase64(thumb)
        }else  if (uploadType.equals("g", true)) {
            val thumb = ThumbnailUtils.createVideoThumbnail(origanPath, MediaStore.Images.Thumbnails.MINI_KIND)
            thumbnailBase64 = getBase64(thumb!!)
        }

        mParms["user_id"] = utils.toRequestBody(sharedPref.getString(Constants.USER_ID))
        mParms["caption"] = utils.toRequestBody(editTextTitle.text.toString())
        mParms["upload_type"] = utils.toRequestBody(uploadType)
        mParms["thumbnail"] = utils.toRequestBody(thumbnailBase64)
        mParms["tagUsers"] = utils.toRequestBody(tagUser.trim())

        viewModel.uploadFeed(mParms, post!!)
    }


    private fun getBase64(thumb: Bitmap): String {

        val byteArrayOutputStream = ByteArrayOutputStream()
        thumb.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT)

    }

    private fun initObserver() {
        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayoutUpload, message!!)
            }
        })

        viewModel.feedUploaded!!.observe(viewLifecycleOwner, Observer<UploadPostModel> { t ->
            if (t?.response?.status?.equals("0")!!) {
                baseshowFeedbackMessage(rootLayoutUpload, t.response?.message!!)
            } else {
                val intent = Intent(activity!!, StartActivity::class.java)
                intent.putExtra("key", "uploadFeed")
                activity!!.startActivity(intent)
                activity!!.finishAffinity()
            }
        })

        viewModel.tagSuggestion!!.observe(viewLifecycleOwner, Observer<TagSuggestion> { t ->
            if (t?.response?.status?.equals("1")!!) {
                rvSuggestion.visibility = View.VISIBLE

                val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                rvSuggestion.layoutManager = layoutManager
                val adapter = SuggestionAdapter(t.response?.data, object : SuggestionAdapter.ItemClick {
                    override fun onSuggestionClick(username: String?) {
                        rvSuggestion.visibility = View.GONE

                        val words = editTextTitle.text?.split("\\s".toRegex())!!.toMutableList()
                        if (words[words.size - 1].trim().isNotEmpty() && words[words.size - 1].trim().length > 1 && words[words.size - 1].trim().take(1) == "@") {
                            words[words.size - 1].replace(words[words.size - 1], "@" + username)
                            words.removeAt(words.size - 1)
                            words.add("@" + username)
                            val sb = StringBuffer()
                            for (item in words) {
                                sb.append(item + " ")
                            }
                            val str = sb.toString()
                            editTextTitle.setText(str)
                            editTextTitle.setSelection(editTextTitle.text!!.length)
                        }
                    }

                })
                rvSuggestion.adapter = adapter

            } else {
                rvSuggestion.visibility = View.GONE
            }
        })


        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("Loading...")
                } else {
                    baseHideProgressDialog()
                }
            }
        })

        viewModel.showFeedbackMessage?.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(t: String) {
                baseshowFeedbackMessage(rootLayoutUpload, t)
            }

        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.showFeedbackMessage?.removeObservers(activity!!)
        viewModel.isLoading.removeObservers(activity!!)
        viewModel.tagSuggestion?.removeObservers(activity!!)
        viewModel.feedUploaded?.removeObservers(activity!!)
        viewModel.errorListener?.removeObservers(activity!!)
    }
}