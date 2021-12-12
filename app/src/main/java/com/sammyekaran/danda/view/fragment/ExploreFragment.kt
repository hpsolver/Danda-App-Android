package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.base.BaseContext
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.model.exploreDataResponse.Datum
import com.sammyekaran.danda.model.exploreDataResponse.ExploreResponse
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.NetworkUtils
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.ExploreAdapter
import com.sammyekaran.danda.viewmodel.ExploreViewModel
import im.ene.toro.PlayerSelector
import im.ene.toro.ToroPlayer
import im.ene.toro.widget.Container
import kotlinx.android.synthetic.main.fragment_explore_data.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.collections.ArrayList


class ExploreFragment : BaseFragmentWithoutBinding(), LifecycleObserver, ExploreAdapter.ItemClick{

    var mExploreData: MutableList<Datum> = mutableListOf()


    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var mAdapter: ExploreAdapter
    var viewModel: ExploreViewModel? = null


    var isRefresh = false
    var mPageNo = 1
    var mCurentPage = 0
    var mLastPage = 0
    var subscriptionType = ""

    val sharedPref: SharedPref by inject()
    var respositry: WebServices = WebServices()
    lateinit var exploreResponse: ExploreResponse
    lateinit var containerExplore: Container
    lateinit var selector: PlayerSelector
    var isStop = false

    private var isLastPage = false
    private val totalPage = 10
    private var isLoading = false
    var itemCount = 0
    private var itemClick:ExploreAdapter.ItemClick?=null


    override fun getLayoutId(): Int {
        return R.layout.fragment_explore_data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, BaseContext(mBaseActivity!!)).get(ExploreViewModel::class.java)
        itemClick=this
        initObserver()
        mBaseActivity?.baseShowProgressBar("")
        viewModel?.exploreData(sharedPref.getString(Constants.USER_ID),mPageNo.toString(), activity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!NetworkUtils.isNetworkConnected(activity!!)) {
            baseshowFeedbackMessage(rootLayout, getString(R.string.no_internet))
        }

        findView(view)
        setAdapter()
        listner()
        setRecycleViewScrollListener()
    }

    private fun findView(view: View) {
        containerExplore = view.findViewById(R.id.containerExplore)
        gridLayoutManager = GridLayoutManager(context, 3)
        /* gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
             override fun getSpanSize(position: Int): Int {
                 return if (position % 3 == 2) {
                     2
                 } else {
                     1
                 }
             }
         }*/


        selector = object : PlayerSelector {
            @NonNull
            override fun reverse(): PlayerSelector {
                return this
            }

            override fun select(container: Container, items: MutableList<ToroPlayer>): MutableCollection<ToroPlayer> {
                val toSelect: MutableList<ToroPlayer>
                var count = items.size
                if (count < 1) {
                    toSelect = Collections.emptyList()
                } else {
                    val firstOrder = items[0].playerOrder
                    val span: Int = gridLayoutManager.spanSizeLookup.getSpanSize(firstOrder)
                    count = Math.min(count, gridLayoutManager.spanCount / span)
                    toSelect = ArrayList()
                    for (i in 0 until count) {
                        toSelect.add(items[i])
                    }
                }
                return toSelect
            }
        }
        containerExplore.playerSelector = selector
    }

    private fun listner() {
        swipeRefresh.setOnRefreshListener {
            refresh()
        }

        textViewSearch.setOnClickListener {
            findNavController().navigate(R.id.action_exploreDataFragment_to_searchFragment)
        }
    }

    fun initObserver() {

        viewModel?.getExploreData()?.observe(this, Observer { exploreResponse ->
             mBaseActivity?.baseHideProgressDialog()
            if (exploreResponse.response?.data?.size == 0) {

            } else {

                subscriptionType= exploreResponse.response?.subscriptionType!!
                mCurentPage = mPageNo
                mLastPage = exploreResponse.response?.lastPage!!.toInt()
                if (mCurentPage != 1) mAdapter.removeLoading()
                mAdapter.addItems(exploreResponse.response?.data!!,subscriptionType)
                swipeRefresh.isRefreshing = false
                // check weather is last page or not
                if (mCurentPage < mLastPage) {
                    mAdapter.addLoading()
                } else {
                    isLastPage = true
                }
                isLoading = false
            }


        })
    }

    fun setAdapter() {
        containerExplore.layoutManager = gridLayoutManager
        containerExplore.isNestedScrollingEnabled = false
        mAdapter = ExploreAdapter(mExploreData, selector, viewModel!!,sharedPref.getString(Constants.USER_ID),subscriptionType,itemClick)
        containerExplore.adapter = mAdapter

    }


    private fun setRecycleViewScrollListener() {
        containerExplore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = gridLayoutManager.childCount
                val pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition()
                val totalItemCount = recyclerView.layoutManager!!.itemCount

                //Pagination
                if (dy > 0) {
                    if (mCurentPage < mLastPage && mCurentPage == mPageNo && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        mPageNo += 1
                        viewModel?.exploreData(sharedPref.getString(Constants.USER_ID),mPageNo.toString(), activity!!)
                    }
                }

            }

        })
    }


    private fun refresh() {
        swipeRefresh.isRefreshing = true
        isRefresh = true
        mPageNo = 1
        isLastPage = false;
        mAdapter.clear();
        viewModel?.exploreData(sharedPref.getString(Constants.USER_ID),mPageNo.toString(), activity)


    }


    override fun onDestroyView() {
        super.onDestroyView()


    }

    override fun onLockButtonClick(userId: String) {
        val action = ExploreFragmentDirections.actionExploreDataFragmentToProfileFragment(userId)
        findNavController().navigate(action)
    }
}