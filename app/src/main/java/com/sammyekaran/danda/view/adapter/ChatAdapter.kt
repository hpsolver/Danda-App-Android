package com.sammyekaran.danda.view.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterChatBinding
import com.sammyekaran.danda.model.MessageModel
import kotlinx.android.synthetic.main.adapter_chat.view.*
import java.util.*


class ChatAdapter(list: MutableList<MessageModel>, userId: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var list: List<MessageModel>?
    var userId: String

    init {
        this.list = list
        this.userId=userId
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterChatBinding>(inflater, R.layout.adapter_chat, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list!![position].fromUser.equals(userId)){
            holder.itemView.relativeLayoutMyMsg.visibility=View.VISIBLE
            holder.itemView.relativeLayoutTheirMsg.visibility=View.GONE
            holder.itemView.tvMyMessage.text=list!![position].message
            holder.itemView.tvMyTimeStamp.text=getDate(list!![position].mTime.toLong())

        }else{
            holder.itemView.relativeLayoutMyMsg.visibility=View.GONE
            holder.itemView.relativeLayoutTheirMsg.visibility=View.VISIBLE
            holder.itemView.tvTheirMessage.text=list!![position].message
            holder.itemView.tvTheirTimeStamp.text=getDate(list!![position].mTime.toLong())
        }

    }
    private fun getDate(time: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.setTimeInMillis(time)
        return DateFormat.format("dd-MM-yy hh:mm", cal).toString()
    }
    fun customNotify(message: List<MessageModel>) {
        this.list = message
        notifyDataSetChanged()

    }

    class ViewHolder(binding: AdapterChatBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterChatBinding

        init {
            this.binding = binding
        }
    }


}