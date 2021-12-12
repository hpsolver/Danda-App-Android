package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentFollowingBinding
import com.sammyekaran.danda.model.followUnfollow.FollowUnfollowModel
import com.sammyekaran.danda.model.followlist.Detail
import com.sammyekaran.danda.model.followlist.FollowListModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.FollowUnfollowAdapter
import com.sammyekaran.danda.viewmodel.FollowUnfollowViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_following.*
import kotlinx.android.synthetic.main.unfollow_dialog.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingFragment(userId: String) : BaseFragment<FragmentFollowingBinding>() {

    val viewModel: FollowUnfollowViewModel by viewModel()
    val userId: String = userId
    lateinit var binding: FragmentFollowingBinding
    val sharedPref: SharedPref by inject()
    var id = ""
    var mSearchText = ""
    var mPageNo = 1
    var mLastPage = 0
    lateinit var mAdapter: FollowUnfollowAdapter
    lateinit var layoutManager: LinearLayoutManager
    var data: MutableList<Detail> = mutableListOf<Detail>()
    val respositry: WebServices
    lateinit var followModel: FollowListModel
    private var getFollowerListApiCall: Call<FollowListModel>? = null

    init {
        this.respositry = WebServices()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_following
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel

        if (userId.equals(sharedPref.getString(Constants.USER_ID))) {
            id = ""
        } else {
            id = sharedPref.getString(Constants.USER_ID)

        }

        initObserver()
        setRecyclerViewScrollListener()
        searchListner()

        //1=follower,2=following
        mPageNo = 1
        data.clear()
        setAdapter()
        getFollower(id, mSearchText, "2", mPageNo.toString(), userId)


    }

    private fun searchListner() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mPageNo=1
                getFollower(id, query!!, "2", mPageNo.toString(), userId)
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mPageNo=1
                getFollower(id, newText!!, "2", mPageNo.toString(), userId)
                return false;
            }

        })
    }

    fun initObserver() {

        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayoutFollowing, message!!)
            }
        })


        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("")
                } else {
                    baseHideProgressDialog()
                }
            }
        })

        viewModel.followUnfollow.observe(viewLifecycleOwner, object : Observer<FollowUnfollowModel> {
            override fun onChanged(followUnfollow: FollowUnfollowModel) {
                if (followUnfollow.response?.status.equals("0")) {
                    baseshowFeedbackMessage(rootLayoutFollowing, followUnfollow.response?.message!!)
                } else {
                    mAdapter.customNotify(data)
                }
            }

        })
        viewModel.followUnfllowStatus.observe(viewLifecycleOwner, object : Observer<Detail> {
            override fun onChanged(it: Detail?) {
                if (it?.usersType.equals("0")) {
                    viewModel.followUnfollow(it?.userId!!, sharedPref.getString(Constants.USER_ID), "1")
                } else {
                    showUnfollowDialog(
                            it?.userId!!,
                            it.username,
                            it.profilePic,
                            sharedPref.getString(Constants.USER_ID)
                    )
                }

            }
        })

    }

    fun getFollower(id: String, mSearchText: String, type: String, mPageNo: String, userId: String) {

        if (getFollowerListApiCall != null && getFollowerListApiCall!!.isExecuted) {
            getFollowerListApiCall!!.cancel()
        }
        if (progressBar.visibility != View.VISIBLE) {
            progressBar.visibility = View.VISIBLE
        }

        getFollowerListApiCall = respositry.followList(id, mSearchText, type, mPageNo, userId)
        getFollowerListApiCall!!.enqueue(object : Callback<FollowListModel> {
            override fun onFailure(call: Call<FollowListModel>, t: Throwable) {

                if (call.isCanceled) {
                    Log.d("APICALL", "CANCELLED")
                } else {
                    progressBar.visibility = View.GONE
                    baseshowFeedbackMessage(rootLayoutFollowing, t.message.toString())
                }
            }

            override fun onResponse(call: Call<FollowListModel>, response: Response<FollowListModel>) {
                progressBar.visibility = View.GONE
                followModel = response.body()!!
                if (followModel.response?.status.equals("0")) {
                    SearchFragment.data.clear()
                    data.clear()
                    baseshowFeedbackMessage(rootLayoutFollowing, followModel.response?.message!!)
                    mAdapter.customNotify(data)
                } else {
                    if(mPageNo.equals("1")){
                        data.clear()
                    }
                    data.addAll(followModel.response?.data?.detail!!)
                    mLastPage = followModel.response?.lastPage!!.toInt()
                    mAdapter.customNotify(data)
                }
            }
        })
    }

    fun setAdapter() {
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView?.isNestedScrollingEnabled = false
        mAdapter = FollowUnfollowAdapter(data, viewModel)
        recyclerView?.adapter = mAdapter

    }

    fun setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    val totalItemCount = recyclerView.layoutManager!!.itemCount
                    if (/*!viewModel.isLoading.value!! &&*/ (visibleItemCount + pastVisiblesItems) >= totalItemCount && mPageNo < mLastPage) {
                        mPageNo = mPageNo + 1
                        getFollower(id, mSearchText, "2", mPageNo.toString(), userId)
                    }
                }

            }
        })
    }

    fun showUnfollowDialog(
            userId: String,
            username: String?,
            profilePic: String?,
            currentUserId: String
    ) {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.unfollow_dialog, null)
        dialogBuilder.setView(dialogView)

        val b = dialogBuilder.create()
        b.show()

        Glide.with(activity!!)
                .load(profilePic)
                .error(R.drawable.ic_icon_avatar)
                .into(dialogView.imageViewProfile)

        var message = "Unfollow " + "<b>" + "@" + username + "</b> " + "?"
        dialogView.imageViewProfile
        dialogView.tvMessage.text = Html.fromHtml(message)
        dialogView.tvCancel.setOnClickListener { b.dismiss() }
        dialogView.tvUnfollow.setOnClickListener {
            b.dismiss()
            viewModel.followUnfollow(userId, currentUserId, "2")
        }


    }

}