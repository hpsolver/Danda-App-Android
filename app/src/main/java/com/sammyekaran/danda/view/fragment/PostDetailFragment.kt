package com.sammyekaran.danda.view.fragment

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.view.ZoomableExoPlayerView
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentPostDetailBinding
import com.sammyekaran.danda.model.homefeed.Link
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.postdetail.PostDetailModel
import com.sammyekaran.danda.model.splitPayment.SplitPaymentResponse
import com.sammyekaran.danda.utils.*
import com.sammyekaran.danda.view.adapter.PostCommentAdapter
import com.sammyekaran.danda.viewmodel.PostDetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.adapter_home.imageViewLike
import kotlinx.android.synthetic.main.dialog_more.view.textViewReport
import kotlinx.android.synthetic.main.dialog_post_detail.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_post_detail.*
import kotlinx.android.synthetic.main.fragment_post_detail.progressBar
import kotlinx.android.synthetic.main.view_product_link.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>(), Player.EventListener {


    lateinit var imageZoomHelper: ImageZoomHelper
    val commonUtils: CommonUtils by inject()

    lateinit var binding: FragmentPostDetailBinding
    val viewModel: PostDetailViewModel by viewModel()
    val sharedPref: SharedPref by inject()
    val commanUtil: CommonUtils by inject()
    val args: PostDetailFragmentArgs by navArgs()
    var mPostDetailModel = PostDetailModel()

    private lateinit var simpleExoplayer: SimpleExoPlayer
    var exoPlayerView: ZoomableExoPlayerView? = null
    private var playbackPosition = 0L
    private val bandwidthMeter by lazy {
        DefaultBandwidthMeter()
    }
    private val adaptiveTrackSelectionFactory by lazy {
        AdaptiveTrackSelection.Factory(bandwidthMeter)
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_post_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        binding.viewModel = viewModel
        imageZoomHelper = ImageZoomHelper(activity)
        exoPlayerView = view.findViewById(R.id.zoomPlayerView)
        activity!!.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        if (sharedPref.getString(Constants.USER_ID).isEmpty()) {
            val action = PostDetailFragmentDirections.actionPostDetailFragmnetToLoginFragment()
            findNavController().navigate(action)
        }
        if (mPostDetailModel.response == null)
            viewModel.postDetail(args.postid, sharedPref.getString(Constants.USER_ID))
        editTextPostListner()
        videoListener()
    }

    private fun editTextPostListner() {
        editTextComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    textViewPost.visibility = View.VISIBLE
                } else if (s.toString().isEmpty()) {
                    textViewPost.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        textViewPost.setOnClickListener {
            viewModel.postComment(sharedPref.getString(Constants.USER_ID), args.postid, editTextComment.text.toString())
            editTextComment.setText("")
            commanUtil.hideSoftKeyBoard(activity!!)
        }
    }

    override fun onStart() {
        super.onStart()
        initializeExoplayer()
    }

    override fun onStop() {
        super.onStop()
        releaseExoplayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.unregisterReceiver(onDownloadComplete)
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoplayer.release();
    }

    private fun videoListener() {

        imageViewPlay.setOnClickListener {
            imageViewPlay.visibility = View.GONE
            Handler().postDelayed({
                imageViewPost.visibility = View.GONE
            }, 1500)

            videoViewContainer.visibility = View.VISIBLE
            prepareExoplayer()
            if (mPostDetailModel.response?.data?.isView != null && mPostDetailModel.response!!.data!!.equals("0")) {
                viewModel.postView(args.postid)
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        listener()
    }

    @SuppressLint("SetTextI18n")
    private fun listener() {
        imageViewMore.setOnClickListener {
            showOptionDialog()
        }

        imageViewDownload.setOnClickListener {
            viewModel.viewDownload(it, activity!!, mPostDetailModel.response?.data?.posts!!, mPostDetailModel.response?.data?.postsType!!)
        }
        imageViewShare.setOnClickListener{
            shareDialog(sharedPref.getString(Constants.USER_ID), mPostDetailModel.response?.data?.postId, mPostDetailModel.response?.data?.profilePic!!, sharedPref.getString(Constants.FULL_NAME))
        }

        imageViewLike.setOnClickListener {
            var isLike = ""
            if (mPostDetailModel.response?.data?.likes.equals("0")) {
                isLike = "1"
                imageViewLike.setImageResource(R.drawable.ic_icon_like_red)
                mPostDetailModel.response?.data?.likesCount =
                        (mPostDetailModel.response?.data?.likesCount)?.toInt()?.plus(1).toString()
                if (mPostDetailModel.response?.data?.postsType.equals("i", true)||mPostDetailModel.response?.data?.postsType.equals("g", true))
                    textViewLikes.text = mPostDetailModel.response?.data?.likesCount + " Likes"
            } else {
                isLike = "0"
                imageViewLike.setImageResource(R.drawable.ic_icon_like)
                mPostDetailModel.response?.data?.likesCount =
                        (mPostDetailModel.response?.data?.likesCount)?.toInt()?.minus(1).toString()
                if (mPostDetailModel.response?.data?.postsType.equals("i", true)||mPostDetailModel.response?.data?.postsType.equals("g", true))
                    textViewLikes.text = mPostDetailModel.response?.data?.likesCount + " Likes"
            }

            mPostDetailModel.response?.data?.likes = isLike
            viewModel.likeDislike(
                    sharedPref.getString(Constants.USER_ID),
                    mPostDetailModel.response?.data?.postId!!,
                    isLike
            )
        }
    }

    private fun shareDialog(profileId: String, postId: String?, profilePic: String, fullname: String) {
        val shareBottomDialogFragment: ShareBottomDialogFragment = ShareBottomDialogFragment.newInstance(profileId, postId, profilePic, fullname)
        shareBottomDialogFragment.show(activity!!.supportFragmentManager, ShareBottomDialogFragment.TAG)
    }

    private fun initObserver() {
        viewModel.detail!!.observe(viewLifecycleOwner, object : Observer<PostDetailModel> {
            override fun onChanged(data: PostDetailModel?) {
                if (data?.response?.status.equals("0")) {
                    baseshowFeedbackMessage(rootLayoutDetail, data?.response?.message!!)
                } else {
                    binding.post = data?.response?.data
                    mPostDetailModel = data!!


                    setAdapter()
                    if (data.response?.data?.likes.equals("0")) {
                        imageViewLike.setImageResource(R.drawable.ic_icon_like)
                    } else {
                        imageViewLike.setImageResource(R.drawable.ic_icon_like_red)
                    }
                    if (mPostDetailModel.response?.data?.postsType.equals("i", true)||mPostDetailModel.response?.data?.postsType.equals("g", true)) {
                        textViewLikes.text = data.response?.data?.likesCount + " Likes"
                    } else {
                        textViewLikes.text = data.response?.data?.totalViews + " Views"
                    }


                    if (data.response?.data?.postsType.equals("I",true)||data.response?.data?.postsType.equals("G",true)) {
                        // set zoomable tag on views that is to be zoomed
                        ImageZoomHelper.setViewZoomable(imageViewPost)
                        loadImage(imageViewPost, data.response?.data?.posts)
                    } else {
                        imageViewPlay.visibility = View.VISIBLE
                        loadImage(imageViewPost, data.response?.data?.thumbnailUrl)
                    }
                }

            }
        })

        viewModel.deletePost?.observe(viewLifecycleOwner, Observer<CommonModel> { t ->
            if (t?.response?.status.equals("1")) {
                findNavController().popBackStack()
            } else {
                baseshowFeedbackMessage(rootLayoutDetail, t?.response?.message!!)
            }
        })
        viewModel.donate?.observe(viewLifecycleOwner, object : Observer<SplitPaymentResponse> {
            override fun onChanged(data: SplitPaymentResponse) {
                if (data.response?.status.equals("1")) {
                } else {
                    baseshowFeedbackMessage(rootLayoutDetail, data.response?.message!!)
                }

            }
        })



        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayoutDetail, message!!)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("")
                } else {
                    baseHideProgressDialog()
                }
            }
        })
    }

    private fun setAdapter() {
        rvComments.layoutManager = LinearLayoutManager(activity)
        rvComments?.isNestedScrollingEnabled = false
        rvComments?.adapter = PostCommentAdapter(mPostDetailModel.response?.data?.comments, viewModel)
    }



    private fun loadImage(imageViewPostProfile: BlurImageView, postUrl: String?) {
        Glide.with(imageViewPostProfile)
                .asBitmap()
                .load(postUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imageViewPostProfile.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
    }

    fun showOptionDialog() {

        val dialogBuilder = AlertDialog.Builder(activity!!)
        val layoutInflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = layoutInflater.inflate(com.sammyekaran.danda.R.layout.dialog_post_detail, null)
        dialogBuilder.setView(dialogView)
        val b = dialogBuilder.create()
        b.show()

        if (mPostDetailModel.response?.data?.userId.equals(sharedPref.getString(Constants.USER_ID))) {
            dialogView.textViewDeletePost.visibility = View.VISIBLE
        } else {
            dialogView.textViewDeletePost.visibility = View.GONE
        }

        dialogView.textViewReport.setOnClickListener {
            b.dismiss()
            val action = PostDetailFragmentDirections.actionPostDetailFragmentToReportFragment(
                    args.postid,
                    mPostDetailModel.response?.data?.userId!!
            )
            findNavController().navigate(action)
        }
        dialogView.textViewCopyUrl.setOnClickListener {
            val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val copyUri: Uri = Uri.parse(mPostDetailModel.response?.data?.copyUrl)
            val clip = ClipData.newUri(activity?.contentResolver, "URI", copyUri)
            clipboard.primaryClip?.addItem(clip.getItemAt(0))
            b.dismiss()
            Toast.makeText(context, "URL Copied !!", Toast.LENGTH_SHORT).show()


        }
        dialogView.textViewDeletePost.setOnClickListener {
            b.dismiss()
            viewModel.deletePost(args.postid)
        }

    }


    private fun initializeExoplayer() {
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(
                activity,
                DefaultRenderersFactory(activity),
                DefaultTrackSelector(adaptiveTrackSelectionFactory),
                DefaultLoadControl()
        )
    }

    private fun releaseExoplayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()

    }


    private fun buildMediaSource(uri: Uri): MediaSource {
        val videoSource = ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("ua")).createMediaSource(uri)
        return ConcatenatingMediaSource(videoSource)
    }


    private fun prepareExoplayer() {
        val uri = Uri.parse(mPostDetailModel.response?.data?.posts)
        val mediaSource = buildMediaSource(uri)
        simpleExoplayer.prepare(mediaSource)
        exoPlayerView?.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY)
            progressBar.visibility = View.INVISIBLE
        else if (playbackState == Player.STATE_ENDED) {
            imageViewPost.visibility = View.VISIBLE
            imageViewPlay.visibility = View.VISIBLE
        }
}

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            baseshowFeedbackMessage(rootLayoutDetail,"Download Completed")
        }
    }
}