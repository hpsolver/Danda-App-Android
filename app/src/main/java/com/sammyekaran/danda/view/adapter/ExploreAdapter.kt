package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseViewHolder
import com.sammyekaran.danda.model.exploreDataResponse.Datum
import com.sammyekaran.danda.viewmodel.ExploreViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.ui.PlayerView
import im.ene.toro.PlayerSelector
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.helper.ToroPlayerHelper
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import kotlinx.android.synthetic.main.adapter_explore.view.*


class ExploreAdapter(list: MutableList<Datum>,
                     playerSelector: PlayerSelector,
                     viewModel: ExploreViewModel,
                     profileId: String,
                     isMember: String,
                     itemClick: ItemClick?) : RecyclerView.Adapter<BaseViewHolder>() {

    var list: MutableList<Datum>?
    var viewModel: ExploreViewModel
    var playerSelector: PlayerSelector
    lateinit var context: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var profileId = ""
    private var subscriptionType = ""
    private var itemClick: ItemClick? = null

    init {
        this.list = list
        this.viewModel = viewModel
        this.playerSelector = playerSelector
        this.profileId = profileId
        this.subscriptionType = isMember
        this.itemClick = itemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseViewHolder {
        context = parent.context

        return when (viewType) {
            VIEW_TYPE_NORMAL -> ExploreViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.adapter_explore, parent, false))
            VIEW_TYPE_LOADING -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.row_progress, parent, false))
            else -> null!!
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)

        holder.itemView.setOnClickListener {
                viewModel.postItemClick(it, list!![position].uploadId!!)
        }
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list?.size!!
    }


    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        holder.setIsRecyclable(false)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        holder.setIsRecyclable(true)
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == list?.size?.minus(1)) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }

    }

    fun customNotify(data: MutableList<Datum>?) {
        this.list = data
        notifyDataSetChanged()
    }


    fun addItems(postItems: List<Datum>?, subscriptionType: String) {
        list?.addAll(postItems!!)
        this.subscriptionType = subscriptionType
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        list?.add(Datum())
        notifyItemInserted(list?.size?.minus(1)!!)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = list?.size!! - 1
        val item: Datum? = getItem(position)
        if (item != null) {
            list?.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        list?.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Datum? {
        return list?.get(position)
    }


    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {

        }

        init {

        }
    }

    inner class ExploreViewHolder(itemView: View?) : BaseViewHolder(itemView), ToroPlayer {

        var playerView: PlayerView
        var helper: ToroPlayerHelper? = null
        lateinit var media: Datum
        private var mediaContainer: FrameLayout
        var imageButtonPlay: ImageButton
        var relativeLayoutBulr: RelativeLayout
        var imageViewPost: ImageView
        var imageExoPlay: ImageButton
        var imageExoPause: ImageButton
        var imageViewArt: ImageView
        lateinit var viewModel: ExploreViewModel
        lateinit var context: Context

        init {
            mediaContainer = itemView!!.findViewById(R.id.mediaContainer)
            imageViewPost = itemView.findViewById(R.id.imageViewExplorePost)
            imageButtonPlay = itemView.findViewById(R.id.imageButtonPlay)
            relativeLayoutBulr = itemView.findViewById(R.id.relativeLayoutBulr)
            playerView = itemView.findViewById(R.id.playerView)
            imageExoPause = itemView.findViewById(R.id.exo_pause)
            imageExoPlay = itemView.findViewById(R.id.exo_play)
            imageViewArt = itemView.findViewById(R.id.imageViewArt)


        }

        override fun onBind(position: Int) {
            super.onBind(position)
            this.media = list!![position]

            imageExoPlay.setOnClickListener {
                imageExoPlay.visibility = View.GONE
                imageViewArt.visibility = View.GONE
                helper?.play()
            }
            imageExoPause.setOnClickListener {
                imageExoPlay.visibility = View.VISIBLE
                imageExoPause.visibility = View.GONE
                helper?.pause()
            }


            imageButtonPlay.setOnClickListener {
                mediaContainer.visibility = View.VISIBLE
                imageViewPost.visibility = View.GONE
            }

            if (media.uploadType.equals("I", true)) {
                imageViewPost.visibility = View.VISIBLE
                mediaContainer.visibility = View.GONE
                imageButtonPlay.visibility = View.GONE




                Glide.with(imageViewPost)
                        .asBitmap()
                        .load(media.upload)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageViewPost.setImageBitmap(resource)

                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })


            } else {
                mediaContainer.visibility = View.VISIBLE
                imageViewPost.visibility = View.GONE
                imageButtonPlay.visibility = View.GONE
                imageExoPause.visibility = View.GONE




                Glide.with(imageViewArt)
                        .asBitmap()
                        .load(media.thumbnail)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageViewArt.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
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
                helper = ExoPlayerViewHelper(this, Uri.parse(media.upload + ""))
            }
            helper!!.initialize(container, playbackInfo)
        }

        override fun play() {
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

    interface ItemClick {
        fun onLockButtonClick(userId: String)
    }
}