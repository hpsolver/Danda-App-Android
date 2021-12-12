package com.sammyekaran.danda.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.profile.Post
import com.sammyekaran.danda.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.adapter_profile.view.*


class PostsAdapter(
        list: List<Post>?,
        profileViewModel: ProfileViewModel,
        userId: String,
        profileId: String,
        isMember: Boolean
) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {


    var list: List<Post>?
    var profileViewModel: ProfileViewModel
    var userId: String
    var profileId: String
    var isMember: Boolean
    lateinit var context: Context


    init {
        this.isMember = isMember
        this.userId = userId
        this.profileId = profileId
        this.list = list
        this.profileViewModel = profileViewModel
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_profile, parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.imageViewPlay.visibility = View.GONE



            if (list?.get(position)?.uploadType.equals("I",true)||list?.get(position)?.uploadType.equals("G",true)) {
                loadImage(holder.imageViewPostProfile, list?.get(position)?.postUrl)
            } else {
                holder.imageViewPlay.visibility = View.VISIBLE
                loadImage(holder.imageViewPostProfile, list?.get(position)?.thumbnailUrl)
            }

        holder.itemView.setOnClickListener {

                profileViewModel.postItemClick(
                        holder.imageViewPostProfile,
                        list?.get(position)?.id!!
                )

        }
    }

    private fun loadImage(imageViewPostProfile: ImageView, postUrl: String?) {
        Glide.with(imageViewPostProfile)
            .load(postUrl)
            .into(imageViewPostProfile)
    }

    fun customNotify(mPostList: List<Post>) {
        this.list = mPostList
        notifyDataSetChanged()

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageViewPostProfile: ImageView
        var imageViewPlay: ImageView

        init {
            this.imageViewPlay = itemView.imageViewPlay
            this.imageViewPostProfile = itemView.imageViewPostProfile
        }
    }



}