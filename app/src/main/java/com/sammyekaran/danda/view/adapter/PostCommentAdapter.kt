package com.sammyekaran.danda.view.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.BR
import com.sammyekaran.danda.R
import com.sammyekaran.danda.databinding.AdapterPostCommentBinding
import com.sammyekaran.danda.model.postdetail.Comment
import com.sammyekaran.danda.utils.CustomTypefaceSpan
import com.sammyekaran.danda.viewmodel.PostDetailViewModel
import kotlinx.android.synthetic.main.adapter_comment.view.*

class PostCommentAdapter(list: List<Comment>?, postCommentViewModel: PostDetailViewModel) :
    RecyclerView.Adapter<PostCommentAdapter.ViewHolder>() {

    var list: List<Comment>?
    var postCommentViewModel: PostDetailViewModel

    init {
        this.list = list
        this.postCommentViewModel = postCommentViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var binding = DataBindingUtil.inflate<AdapterPostCommentBinding>(inflater, R.layout.adapter_post_comment, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.comment = list!!.get(position)
        holder.binding.setVariable(BR.viewModel, postCommentViewModel)

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

    fun customNotify(data: List<Comment>) {
        this.list = data
        notifyDataSetChanged()
    }

    class ViewHolder(binding: AdapterPostCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: AdapterPostCommentBinding

        init {
            this.binding = binding
        }
    }
}