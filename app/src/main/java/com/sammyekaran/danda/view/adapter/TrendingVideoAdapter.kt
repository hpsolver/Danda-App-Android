package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterTagSuggestionBinding
import com.sammyekaran.danda.databinding.AdapterTrendingVideoBinding
import com.sammyekaran.danda.model.trending.Image
import com.sammyekaran.danda.model.trending.Video
import kotlinx.android.synthetic.main.adapter_trending_video.view.*


class TrendingVideoAdapter(list: List<Video>, itemClick: ItemClick) :
        RecyclerView.Adapter<TrendingVideoAdapter.ViewHolder>() {

    var list: List<Video>?
    var itemClick: ItemClick
    var context: Context? = null

    init {
        this.list = list
        this.itemClick = itemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate<AdapterTrendingVideoBinding>(inflater, R.layout.adapter_trending_video, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context!!)
                .load(list?.get(position)?.image)
                .into(holder.itemView.imageView)

        holder.itemView.setOnClickListener {
            itemClick.onItemClick(it,list?.get(position)?.upload_id)
        }
    }

    fun customNotify(data: List<Video>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterTrendingVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterTrendingVideoBinding = binding

    }

    interface ItemClick {
        fun onItemClick(view: View, id: String?)
    }
}