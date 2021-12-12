package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterTicketStatusBinding
import com.sammyekaran.danda.model.fetchticket.DataItem
import com.sammyekaran.danda.viewmodel.SupportViewModel
import kotlinx.android.synthetic.main.adapter_ticket_status.view.*

class TicketAdapter(list: MutableList<DataItem>, supportViewModel: SupportViewModel) :
    RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    var list: List<DataItem>?
    var supportViewModel: SupportViewModel

    init {
        this.list = list
        this.supportViewModel = supportViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterTicketStatusBinding>(inflater, R.layout.adapter_ticket_status, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, supportViewModel)
        if (list?.get(position)?.status.equals("0")){
            holder.itemView.textViewDate.text=list?.get(position)?.created_date
            holder.itemView.textViewViewStatus.text="Pending"
        }else{
            holder.itemView.textViewViewStatus.text="Closed"
            holder.itemView.textViewDate.text=list?.get(position)?.closeDate
        }

    }


    fun customNotify(data: List<DataItem>?) {
        this.list = data!!
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterTicketStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterTicketStatusBinding

        init {
            this.binding = binding
        }
    }
}