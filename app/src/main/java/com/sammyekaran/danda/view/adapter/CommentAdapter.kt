package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.BR
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterCommentBinding
import com.sammyekaran.danda.model.getcomment.Datum
import com.sammyekaran.danda.utils.CustomTypefaceSpan
import com.sammyekaran.danda.viewmodel.CommentViewModel
import kotlinx.android.synthetic.main.adapter_comment.view.*

class CommentAdapter(list: List<Datum>?, commentViewModel: CommentViewModel) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var list: List<Datum>?
    var commentViewModel: CommentViewModel
    var context: Context? = null

    init {
        this.list = list
        this.commentViewModel = commentViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate<AdapterCommentBinding>(inflater, R.layout.adapter_comment, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.comment = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, commentViewModel)

        val username = list?.get(position)?.username + " "
        val comment = list?.get(position)?.comment

        val spannable = SpannableString(username + comment)


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
            username.length + comment?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply the styled label on the TextView
        holder.itemView.tvComment.text = spannable
    }

    fun customNotify(data: List<Datum>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterCommentBinding

        init {
            this.binding = binding
        }
    }
}