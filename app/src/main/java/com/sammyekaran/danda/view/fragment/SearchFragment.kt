package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentSearchBinding
import com.sammyekaran.danda.model.searchuser.Datum
import com.sammyekaran.danda.model.searchuser.SearchUserModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.SearchUserAdapter
import com.sammyekaran.danda.viewmodel.SearchViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.unfollow_dialog.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : BaseFragment<FragmentSearchBinding>() {


    private var searchApiCall: Call<SearchUserModel>? = null
    lateinit var mAdapter: SearchUserAdapter
    lateinit var binding: FragmentSearchBinding
    val viewModel: SearchViewModel by viewModel()
    val sharedPref: SharedPref by inject()
    var respositry: WebServices = WebServices()
    val commonUtils: CommonUtils by inject()
    var mPageNo = 1
    var mLastPage = 0
    var mCurentPage = 0
    lateinit var layoutManager: LinearLayoutManager
    var isRefresh = false
    lateinit var searchUserModel: SearchUserModel
    var mFollowId = ""
    var firstTime = true

    companion object {
        var data: MutableList<Datum> = mutableListOf()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        listener()
        setRecyclerViewScrollListener()
        initObserver()
        if (firstTime) {
            data.clear()
            mPageNo = 1
            mLastPage = 0
            searchUser(sharedPref.getString(Constants.USER_ID), "", mPageNo.toString(), activity!!)
        }
        setAdapter(data)
        searchListener()

    }

    private fun listener() {
        imageButtonBack.setOnClickListener {
            commonUtils.hideSoftKeyBoard(activity!!)
            findNavController().popBackStack()
        }
    }

    private fun searchListener() {
        var isOnTextChanged = false

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isOnTextChanged = !(before == 0 && count == 0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!isOnTextChanged)
                    return
                mPageNo = 1
                searchUser(sharedPref.getString(Constants.USER_ID), s.toString(), mPageNo.toString(), activity!!)
            }

        })

    }

    fun initObserver() {

        viewModel.followUnfollow.observe(viewLifecycleOwner, Observer { followUnfollow ->
            if (followUnfollow.response?.status.equals("0")) {
                baseshowFeedbackMessage(relativeLayoutSearch, followUnfollow.response?.message!!)
            } else {
                for (x in 0 until data.size) {
                    if (data[x].userId.equals(mFollowId)) {
                        if (followUnfollow?.response?.data?.message != null && followUnfollow.response?.data?.message.equals("send follow request successfully"))
                            data[x].userType = "1"
                        else
                            data[x].userType = "0"
                        mFollowId = ""
                        break
                    }
                }
                mAdapter.customNotify(data)

            }
        })


        viewModel.errorListener!!.observe(viewLifecycleOwner, Observer<String> { message -> baseshowFeedbackMessage(relativeLayoutSearch, message!!) })

        viewModel.followUnfollowStatus.observe(viewLifecycleOwner, Observer { it ->
            if (it?.userType.equals("0")) {
                mFollowId = it?.userId!!
                viewModel.followUnfollow(sharedPref.getString(Constants.USER_ID), it.userId!!, "1")
            } else {
                showUnfollowDialog(
                        it?.userId!!,
                        it.username,
                        it.profilePic,
                        sharedPref.getString(Constants.USER_ID)
                )
            }
        })


        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isRefresh)
                    return

                if (isLoading) {
                    baseShowProgressBar("")
                } else {
                    baseHideProgressDialog()
                }
            }
        })

    }

    private fun refreshOnSwap() {
        swapRefresh.isRefreshing = true
        isRefresh = true
        mPageNo = 1
        data.clear()
        searchUser(sharedPref.getString(Constants.USER_ID), "", mPageNo.toString(), activity!!)


    }

    fun searchUser(
            userId: String,
            searchText: String,
            pageNo: String,
            activity: FragmentActivity
    ) {

        if (progressBar.visibility != View.VISIBLE) {
            progressBar.visibility = View.VISIBLE
        }

        if (searchApiCall != null && searchApiCall!!.isExecuted) {
            searchApiCall!!.cancel()
        }

        searchApiCall = respositry.searchUser(userId, searchText, pageNo, activity)
        searchApiCall!!.enqueue(object : Callback<SearchUserModel> {
            override fun onFailure(call: Call<SearchUserModel>, t: Throwable) {
                if (call.isCanceled) {
                    Log.d("APICALL", "CANCELLED")
                } else {
                    progressBar.visibility = View.GONE
                    baseshowFeedbackMessage(relativeLayoutSearch, t.message!!)
                }
            }

            override fun onResponse(call: Call<SearchUserModel>, response: Response<SearchUserModel>) {
                progressBar.visibility = View.GONE
                if (response.body() != null) {
                    searchUserModel = response.body()!!
                    if (swapRefresh == null)
                        return
                    if (isRefresh) {
                        data.clear()
                        swapRefresh.isRefreshing = false
                        isRefresh = false
                    }


                    if (!searchUserModel.response?.status.equals("0")) {
                        mCurentPage = mPageNo
                        if (mPageNo == 1)
                            data.clear()
                        data.addAll(searchUserModel.response?.data!!)
                        mLastPage = searchUserModel.response?.lastPage!!.toInt()
                        mAdapter.customNotify(data)
                    } else {
                        mCurentPage = 1
                        data.clear()
                        mAdapter.customNotify(data)
                    }
                } else {
                    baseshowFeedbackMessage(relativeLayoutSearch, "Server Error !")
                }


            }
        })
    }

    fun setAdapter(data: List<Datum>?) {
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView?.isNestedScrollingEnabled = false
        mAdapter = SearchUserAdapter(data, viewModel)
        recyclerView?.adapter = mAdapter

    }

    fun setRecyclerViewScrollListener() {
        swapRefresh.setOnRefreshListener { refreshOnSwap() }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    val totalItemCount = recyclerView.layoutManager!!.itemCount

                    //Pagination
                    if (dy > 0) {
                        if (mCurentPage < mLastPage && mCurentPage == mPageNo && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            mPageNo = mPageNo + 1
                            searchUser(sharedPref.getString(Constants.USER_ID), "", mPageNo.toString(), activity!!)
                        }
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

        val message = "Unfollow " + "<b>" + "@" + username + "</b> " + "?"
        dialogView.imageViewProfile
        dialogView.tvMessage.text = Html.fromHtml(message)
        dialogView.tvCancel.setOnClickListener { b.dismiss() }
        dialogView.tvUnfollow.setOnClickListener {
            b.dismiss()
            mFollowId = userId
            viewModel.followUnfollow(currentUserId, userId, "2")
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (searchApiCall != null && searchApiCall!!.isExecuted) {
            searchApiCall!!.cancel()
        }
        firstTime = false
        viewModel.followUnfollow.removeObservers(activity!!)
        viewModel.isLoading.removeObservers(activity!!)
        viewModel.errorListener?.removeObservers(activity!!)
        viewModel.followUnfollowStatus.removeObservers(activity!!)
    }
}