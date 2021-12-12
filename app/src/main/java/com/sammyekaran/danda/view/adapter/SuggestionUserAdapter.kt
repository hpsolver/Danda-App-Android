package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterSuggestionBinding
import com.sammyekaran.danda.model.homefeed.Suggestion
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.unfollow_dialog.view.*

class SuggestionUserAdapter(list: List<Suggestion>?, homeViewModel: HomeViewModel) :
        RecyclerView.Adapter<SuggestionUserAdapter.ViewHolder>() {

    var list: List<Suggestion>?
    var homeViewModel: HomeViewModel
    lateinit var context :Context

    init {
        this.list = list
        this.homeViewModel = homeViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<AdapterSuggestionBinding>(inflater, R.layout.adapter_suggestion, parent, false)
         context=parent.context
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.suggestion = list!![position]
        holder.binding.setVariable(BR.viewModel, homeViewModel)

        holder.binding.tvFollowUnfollow.setOnClickListener {
            if (list!![position].userType.equals("0")) {
                list!![position].userType = "1"
                notifyItemChanged(position)
                homeViewModel.followUnfollow(SharedPref(context).getString(Constants.USER_ID), list!![position].userId!!, "1")
            } else if (list!![position].userType.equals("1")) {
                list!![position].userType = "0"
                notifyItemChanged(position)
                showUnfollowDialog(
                        list!![position].userId!!,
                        list!![position].username,
                        list!![position].profilePic,
                        SharedPref(context).getString(Constants.USER_ID)
                )
            }
        }
    }

    fun customNotify(data: List<Suggestion>) {
        this.list = data
        notifyDataSetChanged()
    }

    fun showUnfollowDialog(
            userId: String,
            username: String?,
            profilePic: String?,
            currentUserId: String
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.unfollow_dialog, null)
        dialogBuilder.setView(dialogView)

        val b = dialogBuilder.create()
        b.show()

        Glide.with(context)
                .load(profilePic)
                .error(R.drawable.ic_icon_avatar)
                .into(dialogView.imageViewProfile)

        var message = "Unfollow " + "<b>" + "@" + username + "</b> " + "?"
        dialogView.imageViewProfile
        dialogView.tvMessage.text = Html.fromHtml(message)
        dialogView.tvCancel.setOnClickListener { b.dismiss() }
        dialogView.tvUnfollow.setOnClickListener {
            b.dismiss()
            homeViewModel.followUnfollow(currentUserId, userId, "2")
        }


    }

    class ViewHolder(binding: AdapterSuggestionBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterSuggestionBinding = binding

    }
}