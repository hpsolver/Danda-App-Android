package com.sammyekaran.danda.view.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.ui.PlayerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseViewHolder
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.BlurImageView
import com.sammyekaran.danda.utils.ImageZoomHelper
import com.sammyekaran.danda.viewmodel.AllTrendingViewModel
import com.sammyekaran.danda.viewmodel.HomeViewModel
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoPlayerDispatcher
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.helper.ToroPlayerHelper
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import im.ene.toro.widget.PressablePlayerSelector
import kotlinx.android.synthetic.main.adapter_home.view.*
import kotlinx.android.synthetic.main.dialog_more.view.*

class AllTrendingAdapter(list: MutableList<com.sammyekaran.danda.model.homefeed.Result>?,
                         viewModel: AllTrendingViewModel,
                         prodileId: String, pressablePlayerSelector: PressablePlayerSelector) : RecyclerView.Adapter<BaseViewHolder>() {
    var list: MutableList<com.sammyekaran.danda.model.homefeed.Result>?
    var viewModel: AllTrendingViewModel
    var profileId: String
    var pressablePlayerSelector: PressablePlayerSelector
    lateinit var context: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    var respositry: WebServices = WebServices()

    init {
        this.list = list
        this.viewModel = viewModel
        this.profileId = prodileId
        this.pressablePlayerSelector = pressablePlayerSelector
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.adapter_home, parent, false))
            VIEW_TYPE_LOADING -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.row_progress, parent, false))
            else -> null!!
        }

    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        holder.onBind(position)

    }


    override fun getItemCount(): Int {
        return list!!.size
    }


    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        holder.setIsRecyclable(false)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        holder.setIsRecyclable(true)
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == list?.size?.minus(1)) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    fun customNotify(
            data: List<com.sammyekaran.danda.model.homefeed.Result>
    ) {
        list?.addAll(data)
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): com.sammyekaran.danda.model.homefeed.Result? {
        return list?.get(position)
    }

    fun addLoading() {
        isLoaderVisible = true
        list?.add(com.sammyekaran.danda.model.homefeed.Result())
        notifyItemInserted(list?.size?.minus(1)!!)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = list?.size!! - 1
        val item: com.sammyekaran.danda.model.homefeed.Result? = getItem(position)
        if (item != null) {
            list?.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {

        }

        init {

        }
    }

    inner class ViewHolder(itemView: View?) : BaseViewHolder(itemView), ToroPlayer {
        var playerView: PlayerView
        var helper: ToroPlayerHelper? = null
        lateinit var media: com.sammyekaran.danda.model.homefeed.Result
        var mediaContainer: FrameLayout
        var relativeLayout: RelativeLayout
        var imageButtonPlay: ImageButton
        var imageViewPostProfile: ImageView
        var textViewLikes: TextView
        var imageViewLike: ImageView
        var imageButtonMore: ImageButton
        var imageViewComment: ImageView
        var imageViewMyProfile: ImageView
        var imageViewPost: BlurImageView
        var textViewUserName: TextView
        var textViewFullName: TextView
        var textViewCaption: TextView
        var tvViewAllComments: TextView
        var imageExoPlay: ImageButton
        var imageExoPause: ImageButton
        var editTextComment: EditText
        var textViewPost: TextView
        var imageViewArt: ImageView
        var imageViewDownload: ImageView
        var rlShared: RelativeLayout


        init {
            rlShared = itemView!!.findViewById(R.id.rlShared)
            mediaContainer = itemView.findViewById(R.id.mediaContainer)
            imageViewDownload = itemView.findViewById(R.id.imageViewDownload)
            imageViewPostProfile = itemView.findViewById(R.id.imageViewPostProfile)
            imageViewMyProfile = itemView.findViewById(R.id.imageViewMyProfile)
            textViewUserName = itemView.findViewById(R.id.textViewUserName)
            textViewFullName = itemView.findViewById(R.id.textViewFullname)
            relativeLayout = itemView.findViewById(R.id.relativeLayout)
            textViewCaption = itemView.findViewById(R.id.textViewCaption)
            imageViewPost = itemView.findViewById(R.id.imageViewHomePost)
            imageViewLike = itemView.findViewById(R.id.imageViewLike)
            imageButtonMore = itemView.findViewById(R.id.ibMore)
            tvViewAllComments = itemView.findViewById(R.id.tvViewAllComments)
            imageViewComment = itemView.findViewById(R.id.imageViewComment)
            imageButtonPlay = itemView.findViewById(R.id.imageButtonPlay)
            editTextComment = itemView.findViewById(R.id.editTextComment)
            textViewPost = itemView.findViewById(R.id.textViewPost)
            textViewLikes = itemView.findViewById(R.id.textViewLikes)
            playerView = itemView.findViewById(R.id.playerView)
            imageExoPause = itemView.findViewById(R.id.exo_pause)
            imageExoPlay = itemView.findViewById(R.id.exo_play)
            imageViewArt = itemView.findViewById(R.id.imageViewArt)
            playerView.setControlDispatcher(
                    ExoPlayerDispatcher(
                            pressablePlayerSelector,
                            this
                    )
            )

        }

        override fun onBind(position: Int) {
            super.onBind(position)

            this.media = list!![position]

            rlShared.visibility=View.GONE

            if (list?.size == 0 || (list!!.isNotEmpty() && position == 0)) {
                itemView.recyclerViewSuggestion.visibility = View.VISIBLE
                itemView.textViewSuggestion.visibility = View.VISIBLE
            } else {
                itemView.recyclerViewSuggestion.visibility = View.GONE
                itemView.textViewSuggestion.visibility = View.GONE
            }
            itemView.setOnLongClickListener(pressablePlayerSelector)

            Glide.with(imageViewPostProfile)
                    .load(media.followerProfile)
                    .error(R.drawable.ic_icon_avatar)
                    .placeholder(R.drawable.ic_icon_avatar)
                    .into(imageViewPostProfile)


            Glide.with(imageViewMyProfile)
                    .load(media.profilePic)
                    .error(R.drawable.ic_icon_avatar)
                    .placeholder(R.drawable.ic_icon_avatar)
                    .into(imageViewMyProfile)


            textViewFullName.text = media.username
            textViewUserName.text = media.username
            textViewCaption.text = media.caption
            if (media.postsType.equals("i")||media.postsType.equals("G")) {
                textViewLikes.text = media.likesCount + " Likes"
            } else {
                textViewLikes.text = media.totalViews + " Views"
            }

            imageViewDownload.setOnClickListener {
                viewModel.viewDownload(it, context, media.posts!!, media.postsType)
            }



            if (media.likes.equals("1")) {
                imageViewLike.setImageResource(R.drawable.ic_icon_like_red)
            } else {
                imageViewLike.setImageResource(R.drawable.ic_icon_like)
            }

            imageExoPlay.setOnClickListener {
                imageExoPlay.visibility = View.GONE
                imageViewArt.visibility = View.GONE
                helper?.play()
                if (media.isView.equals("0"))
                    viewModel.postView(media.postId!!, profileId)
            }
            imageExoPause.setOnClickListener {
                imageExoPlay.visibility = View.VISIBLE
                imageExoPause.visibility = View.GONE
                helper?.pause()
            }


            imageViewLike.setOnClickListener {

                viewModel.likeDislikeClick(it, media, textViewLikes, profileId)
            }
            imageButtonMore.setOnClickListener(View.OnClickListener {
                showOptionDialog()
            })
            imageViewComment.setOnClickListener(View.OnClickListener {
                viewModel.viewComment(it, media.postId!!)
            })
            tvViewAllComments.setOnClickListener(View.OnClickListener {
                viewModel.viewComment(it, media.postId!!)
            })
            relativeLayout.setOnClickListener(View.OnClickListener {
                viewModel.viewProfile(it, media.userId!!)
            })

            textViewPost.setOnClickListener {
                viewModel.postComment(media.userId!!, media.postId!!, editTextComment.text.toString())
                editTextComment.setText("")
            }


            imageButtonPlay.setOnClickListener {
                mediaContainer.visibility = View.VISIBLE
                imageViewPost.visibility = View.GONE
            }

            if (media.postsType.equals("I", true)) {
                //Zoom
                ImageZoomHelper.setViewZoomable(imageViewPost)
                imageViewPost.visibility = View.VISIBLE
                mediaContainer.visibility = View.GONE
                imageButtonPlay.visibility = View.GONE



                Glide.with(imageViewPost)
                        .asBitmap()
                        .load(media.posts)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageViewPost.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })

            }
            else if (media.postsType.equals("G")) {
                imageViewPost.visibility = View.VISIBLE
                mediaContainer.visibility = View.GONE
                imageButtonPlay.visibility = View.GONE

                Glide.with(imageViewPost)
                        .load(media.posts)
                        .into(imageViewPost)

            } else {
                mediaContainer.visibility = View.VISIBLE
                imageViewPost.visibility = View.GONE
                imageButtonPlay.visibility = View.GONE
                imageExoPause.visibility = View.GONE
                Glide.with(imageViewArt)
                        .asBitmap()
                        .load(media.thumbnailUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageViewArt.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
            }

            editTextComment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length > 0) {
                        textViewPost.visibility = View.VISIBLE
                    } else if (s.toString().isEmpty()) {
                        textViewPost.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })


        }


        private fun showOptionDialog() {

            val dialogBuilder = AlertDialog.Builder(context)
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = layoutInflater.inflate(R.layout.dialog_more, null)
            dialogBuilder.setView(dialogView)

            val b = dialogBuilder.create()
            b.show()

            dialogView.textViewCopyUrls.setOnClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyUri: Uri = Uri.parse("www.google.com")/*Uri.parse(media.copyUrl)*/
                val clip = ClipData.newUri(context.contentResolver, "URI", copyUri)
                clipboard.primaryClip?.addItem(clip.getItemAt(0))
                b.dismiss()
                Toast.makeText(context, "URL Copied !!", Toast.LENGTH_SHORT).show()

            }

            dialogView.textViewReport.setOnClickListener {
                b.dismiss()
                viewModel.report(itemView, media.postId, media.userId)
            }

        }


        @NonNull
        override fun getPlayerView(): View {
            return playerView
        }

        override fun getCurrentPlaybackInfo(): PlaybackInfo {
            return if (helper != null) helper!!.latestPlaybackInfo else PlaybackInfo()
        }

        override fun initialize(container: Container, playbackInfo: PlaybackInfo) {
            if (helper == null) {
                helper = ExoPlayerViewHelper(this, Uri.parse(media.posts + ""))
            }
            helper!!.initialize(container, playbackInfo)
        }

        override fun play() {
            imageViewArt.visibility = View.VISIBLE
            imageExoPlay.visibility = View.VISIBLE
            helper?.addPlayerEventListener(playerListner)
        }

        private val playerListner = object : ToroPlayer.EventListener {
            override fun onFirstFrameRendered() {
            }

            override fun onBuffering() {
            }

            override fun onPlaying() {
            }

            override fun onPaused() {
            }

            override fun onCompleted() {
                imageExoPlay.visibility = View.VISIBLE
                imageViewArt.visibility = View.VISIBLE
                playerView.player.seekTo(0)
                helper?.pause()
            }

        }

        override fun pause() {
            if (helper != null) helper!!.pause()
        }

        override fun isPlaying(): Boolean {
            return helper != null && helper!!.isPlaying
        }

        override fun release() {
            if (helper != null) {
                helper!!.release()
                helper = null
            }
        }

        override fun wantsToPlay(): Boolean {
            return ToroUtil.visibleAreaOffset(this, itemView.parent) >= 0.85
        }

        override fun getPlayerOrder(): Int {
            return adapterPosition
        }

        override fun clear() {

        }


        override fun toString(): String {
            return "ExoPlayer{" + hashCode() + " " + adapterPosition + "}"
        }


    }

    fun clear() {
        list?.clear()
        notifyDataSetChanged()
    }


}