package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sammyekaran.danda.BR
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterNotificationBinding
import com.sammyekaran.danda.model.notification.Datum
import com.sammyekaran.danda.utils.CustomTypefaceSpan
import com.sammyekaran.danda.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.adapter_notification.view.*


class NotificationAdapter(list: List<Datum>?, notificationViewModel: NotificationViewModel) :
        RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    var list: List<Datum>?
    var context: Context? = null
    var notificationViewModel: NotificationViewModel

    init {
        this.list = list
        this.notificationViewModel = notificationViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding =
                DataBindingUtil.inflate<AdapterNotificationBinding>(
                        inflater,
                        R.layout.adapter_notification,
                        parent,
                        false
                )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.notification = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, notificationViewModel)
        var username = ""
        if (list?.get(position)?.userId.equals("0")) {
            Glide.with(context!!)
                    .load(R.drawable.ic_logo)
                    .into(holder.itemView.ivProfile)
            username = "Support :" + " "
        } else {
            Glide.with(context!!)
                    .load(list?.get(position)?.profilePic)
                    .error(R.drawable.ic_icon_avatar)
                    .into(holder.itemView.ivProfile)
            username = list?.get(position)?.username + " "
        }


        val notification = list?.get(position)?.notification + " "
        val caption = list?.get(position)?.caption


        val spannable = SpannableString(username + notification + caption)

        holder.itemView.ivProfile.setOnClickListener {
            if (list?.get(position)?.userId.equals("0")) {
                notificationViewModel.navigateToNotificationDetail(it,list!![position].notification!!)
            }else{
                notificationViewModel.navigateToProfile(it, list?.get(position)!!)
            }
        }

        val usernameClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                if (list?.get(position)?.userId.equals("0")) {
                    notificationViewModel.navigateToNotificationDetail(textView,list!![position].notification!!)
                }else{
                    notificationViewModel.navigateToProfile(textView, list?.get(position)!!)
                }

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds);
                ds.isUnderlineText = false;
            }

        }
        spannable.setSpan(usernameClick, 0, username.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the custom typeface to span over a section of the spannable object
        spannable.setSpan(
                CustomTypefaceSpan("montserratmedium", Typeface.DEFAULT_BOLD),
                0,
                username.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
                CustomTypefaceSpan("montserratreguler", Typeface.DEFAULT),
                username.length,
                username.length + notification.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        spannable.setSpan(
                CustomTypefaceSpan("montserratmedium", Typeface.DEFAULT_BOLD),
                username.length + notification.length,
                username.length + notification.length + caption?.length!!,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )




        holder.itemView.tvNotification.text = spannable
        holder.itemView.tvNotification.setMovementMethod(LinkMovementMethod.getInstance());


    }


    fun customNotify(data: List<Datum>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterNotificationBinding

        init {
            this.binding = binding
        }
    }
}