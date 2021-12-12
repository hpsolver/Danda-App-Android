package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterSupportConversationBinding
import com.sammyekaran.danda.model.fetchsupportchat.DataItem
import com.sammyekaran.danda.viewmodel.SupportViewModel
import kotlinx.android.synthetic.main.adapter_support_conversation.view.*

class SupportConversationAdapter(list: MutableList<DataItem>, supportViewModel: SupportViewModel) :
    RecyclerView.Adapter<SupportConversationAdapter.ViewHolder>() {

    var list: List<DataItem>?
    var supportViewModel: SupportViewModel

    init {
        this.list = list
        this.supportViewModel = supportViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterSupportConversationBinding>(
            inflater,
            R.layout.adapter_support_conversation,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, supportViewModel)

        if (list?.get(position)?.flag.equals("0")) {
            holder.itemView.llsenderView.visibility = View.VISIBLE
            holder.itemView.llreciverView.visibility = View.GONE
            holder.itemView.textViewSend.text=list?.get(position)?.message
            holder.itemView.textViewSendTime.text=list?.get(position)?.created
        } else {
            holder.itemView.llreciverView.visibility = View.VISIBLE
            holder.itemView.llsenderView.visibility = View.GONE
            holder.itemView.textViewReceived.text=list?.get(position)?.message
            holder.itemView.textViewRecivedTime.text=list?.get(position)?.created
        }


    }


    fun customNotify(data: List<DataItem>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterSupportConversationBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterSupportConversationBinding

        init {
            this.binding = binding
        }
    }
}