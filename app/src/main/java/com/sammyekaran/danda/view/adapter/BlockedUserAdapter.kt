package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterBlockedUserBinding
import com.sammyekaran.danda.model.blockuserlist.DataItem
import com.sammyekaran.danda.viewmodel.BlockedViewModel

class BlockedUserAdapter(list: List<DataItem>?, blockedViewModel: BlockedViewModel) :
    RecyclerView.Adapter<BlockedUserAdapter.ViewHolder>() {

    var list: List<DataItem>?
    var blockedViewModel: BlockedViewModel

    init {
        this.list = list
        this.blockedViewModel= blockedViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterBlockedUserBinding>(inflater, R.layout.adapter_blocked_user, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, blockedViewModel)
    }



    class ViewHolder(binding: AdapterBlockedUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterBlockedUserBinding

        init {
            this.binding = binding
        }
    }
}