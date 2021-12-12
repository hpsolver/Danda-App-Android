package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterReportBinding
import com.sammyekaran.danda.model.ReportDetailModel
import com.sammyekaran.danda.viewmodel.ReportViewModel

class ReportAdapter(list: MutableList<ReportDetailModel>, reportViewModel: ReportViewModel,itemClick:ItemClick) :
    RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    var list: List<ReportDetailModel>?
    var reportViewModel: ReportViewModel
    var itemClick:ItemClick

    init {
        this.itemClick=itemClick
        this.list = list
        this.reportViewModel = reportViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterReportBinding>(inflater, R.layout.adapter_report, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, reportViewModel)
        holder.itemView.setOnClickListener {
            itemClick.onClick(position)
        }
    }



    class ViewHolder(binding: AdapterReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterReportBinding

        init {
            this.binding = binding
        }
    }

    interface ItemClick{
        fun onClick(position: Int)
    }
}