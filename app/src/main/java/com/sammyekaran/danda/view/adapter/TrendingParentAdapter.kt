package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseViewHolder
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.model.trending.Data
import com.sammyekaran.danda.model.trending.Gif
import com.sammyekaran.danda.model.trending.Image
import com.sammyekaran.danda.model.trending.Video
import com.sammyekaran.danda.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.adapter_trending_parent.view.*

class TrendingParentAdapter(viewModel: TrendingViewModel?, data: Data) : RecyclerView.Adapter<BaseViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    lateinit var context: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    var respositry: WebServices = WebServices()
    var data: Data = data;
    var viewModel: TrendingViewModel = viewModel!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseViewHolder {
        context = parent.context
        return TrendingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_trending_parent, parent, false))
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)

    }


    override fun getItemCount(): Int {
        return 3
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
        return VIEW_TYPE_NORMAL
    }


    private fun setImageAdapter(itemView: View, images: List<Image>) {
        val childLayoutManager =
                LinearLayoutManager(itemView.rvTrendingChild.context, RecyclerView.HORIZONTAL, false)
        itemView.rvTrendingChild.apply {
            layoutManager = childLayoutManager
            adapter = TrendingImagesAdapter(images, object : TrendingImagesAdapter.ItemClick {
                override fun onItemClick(view: View, id: String?) {
                    viewModel.viewPostDetail(view, id!!)
                }

            })
            setRecycledViewPool(viewPool)
        }
    }

    private fun setGifAdapter(itemView: View, gif: List<Gif>) {
        val childLayoutManager =
                LinearLayoutManager(itemView.rvTrendingChild.context, RecyclerView.HORIZONTAL, false)
        itemView.rvTrendingChild.apply {
            layoutManager = childLayoutManager
            adapter = TrendingGifAdapter(gif, object : TrendingGifAdapter.ItemClick {
                override fun onItemClick(view: View, id: String?) {
                    viewModel.viewPostDetail(view, id!!)
                }

            })
            setRecycledViewPool(viewPool)
        }
    }

    private fun setVideoAdapter(itemView: View, video: List<Video>) {
        val childLayoutManager =
                LinearLayoutManager(itemView.rvTrendingChild.context, RecyclerView.HORIZONTAL, false)
        itemView.rvTrendingChild.apply {
            layoutManager = childLayoutManager
            adapter = TrendingVideoAdapter(video, object : TrendingVideoAdapter.ItemClick {
                override fun onItemClick(view: View, id: String?) {
                    viewModel.viewPostDetail(view, id!!)
                }

            })
            setRecycledViewPool(viewPool)
        }
    }

    fun customNotify(
            data: Data?
    ) {
        this.data = data!!
        notifyDataSetChanged()
    }


    inner class TrendingViewHolder(itemView: View?) : BaseViewHolder(itemView) {

        override fun onBind(position: Int) {
            super.onBind(position)

            if (position == 0&& data.images.isNotEmpty()) {
                itemView.tvTitle.setText("Trending Images")
                itemView.tvViewAll.visibility=View.VISIBLE
                setImageAdapter(itemView, data.images)
            } else if (position == 1&& data.videos.isNotEmpty()) {
                itemView.tvTitle.setText("Trending Videos")
                itemView.tvViewAll.visibility=View.VISIBLE
                setVideoAdapter(itemView, data.videos)
            } else if (position == 2&& data.gif.isNotEmpty()) {
                itemView.tvTitle.setText("Trending Gif")
                itemView.tvViewAll.visibility=View.VISIBLE
                setGifAdapter(itemView, data.gif)
            }

            itemView.tvViewAll.setOnClickListener {
                var type = ""
                if (position == 0) {
                    type = "I"
                } else if (position == 1) {
                    type = "V"
                } else if (position == 2) {
                    type = "G"
                }
                viewModel.openAllTrendingFragment(it, type)
            }
        }


        override fun toString(): String {
            return "ExoPlayer{" + hashCode() + " " + adapterPosition + "}"
        }

        override fun clear() {

        }


    }


    interface ItemClick {

    }

}