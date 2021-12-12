package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterFollowListBinding
import com.sammyekaran.danda.model.followlist.Detail
import com.sammyekaran.danda.viewmodel.FollowUnfollowViewModel
import java.util.*

class FollowUnfollowAdapter(list: MutableList<Detail>?, followUnfollowViewModel: FollowUnfollowViewModel) :
    RecyclerView.Adapter<FollowUnfollowAdapter.ViewHolder>() {


    var list: MutableList<Detail>?
    var tempList: ArrayList<Detail> = arrayListOf()
    var followUnfollowViewModel: FollowUnfollowViewModel

    init {
        this.list = list
        this.followUnfollowViewModel = followUnfollowViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterFollowListBinding>(inflater, R.layout.adapter_follow_list, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.user = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, followUnfollowViewModel)
    }

    fun customNotify(data: MutableList<Detail>) {
        this.list = data
        this.tempList.addAll(list!!)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
            var query = query.toLowerCase(Locale.getDefault());
            list?.clear()
            if (query.length == 0) {
                list?.addAll(tempList);
            } else {
                for (item in tempList) {
                    if (item.username?.toLowerCase(Locale.getDefault())?.contains(query)!!) {
                        list?.add(item);
                    }
                }
            }
            notifyDataSetChanged();

    }

    class ViewHolder(binding: AdapterFollowListBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterFollowListBinding

        init {
            this.binding = binding
        }
    }
}