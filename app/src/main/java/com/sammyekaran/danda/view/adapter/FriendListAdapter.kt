package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterFriendListBinding
import com.sammyekaran.danda.model.friendList.UserDetail
import com.sammyekaran.danda.viewmodel.FriendsViewModel
import kotlinx.android.synthetic.main.adapter_friend_list.view.*

class FriendListAdapter(list: MutableSet<UserDetail>, friendViewModel: FriendsViewModel) :
    RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    private lateinit var  context: Context
    var list: MutableSet<UserDetail>?
    var friendViewModel: FriendsViewModel

    init {
        this.list = list
        this.friendViewModel = friendViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        context=parent.context
        var binding =
            DataBindingUtil.inflate<AdapterFriendListBinding>(inflater, R.layout.adapter_friend_list, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.friend = list!!.elementAt(position)
        holder.binding.setVariable(BR.viewModel, friendViewModel)

        if (list!!.elementAt(position).msgCount?.equals(0)!!) {
            holder.itemView.tvCount.visibility = View.GONE
        } else if (list!!.elementAt(position).msgCount!! <=99) {
            holder.itemView.tvCount.visibility = View.VISIBLE
            holder.itemView.tvCount.text = list!!.elementAt(position).msgCount.toString()
        }else if (list!!.elementAt(position).msgCount!! >99) {
            holder.itemView.tvCount.visibility = View.VISIBLE
            holder.itemView.tvCount.text =context.getString(R.string.str_99_plus)
        }
    }

    fun customNotify(friendList: MutableSet<UserDetail>) {
        this.list = friendList
        notifyDataSetChanged()

    }

    class ViewHolder(binding: AdapterFriendListBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterFriendListBinding

        init {
            this.binding = binding
        }
    }
}