package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterSearchUserBinding
import com.sammyekaran.danda.model.searchuser.Datum
import com.sammyekaran.danda.viewmodel.SearchViewModel

class SearchUserAdapter(list: List<Datum>?, searchViewModel: SearchViewModel) :
    RecyclerView.Adapter<SearchUserAdapter.ViewHolder>() {

    var list: List<Datum>?
    var searchViewModel: SearchViewModel

    init {
        this.list = list
        this.searchViewModel = searchViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterSearchUserBinding>(inflater, R.layout.adapter_search_user, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.search = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, searchViewModel)
    }

    fun customNotify(data: List<Datum>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterSearchUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterSearchUserBinding

        init {
            this.binding = binding
        }
    }
}