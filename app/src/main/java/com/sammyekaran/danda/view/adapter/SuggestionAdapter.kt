package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterTagSuggestionBinding
import com.sammyekaran.danda.model.tagSuggestion.Datum
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adapter_tag_suggestion.view.*


class SuggestionAdapter(list: List<Datum>?,itemClick:ItemClick) :
    RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    var list: List<Datum>?
    var itemClick:ItemClick
    var context: Context? = null

    init {
        this.list = list
        this.itemClick=itemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate<AdapterTagSuggestionBinding>(inflater, R.layout.adapter_tag_suggestion, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvFullname.text= list?.get(position)?.fullname
        holder.itemView.tvUserName.text= list?.get(position)?.username

        if(list?.get(position)?.profile_pic!!.isNullOrEmpty()){
            Glide.with(context!!)
                    .load(list?.get(position)?.profile_pic)
                    .into(holder.itemView.ivProfile)
        }


        holder.itemView.setOnClickListener {
            itemClick.onSuggestionClick(list?.get(position)?.username)
        }
    }

    fun customNotify(data: List<Datum>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterTagSuggestionBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterTagSuggestionBinding = binding

    }

    interface ItemClick{
        fun onSuggestionClick(username: String?)
    }
}