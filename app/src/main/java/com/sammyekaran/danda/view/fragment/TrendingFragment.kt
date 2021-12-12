package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseContext
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.model.trending.Data
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.NetworkUtils
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.TrendingParentAdapter
import com.sammyekaran.danda.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_trending.*
import org.koin.android.ext.android.inject


class TrendingFragment : BaseFragmentWithoutBinding(), LifecycleObserver, TrendingParentAdapter.ItemClick {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var mAdapter: TrendingParentAdapter
    var viewModel: TrendingViewModel? = null
    val sharedPref: SharedPref by inject()
    var respositry: WebServices = WebServices()
    private var itemClick: TrendingParentAdapter.ItemClick? = null


    override fun getLayoutId(): Int {
        return R.layout.fragment_trending
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, BaseContext(mBaseActivity!!)).get(TrendingViewModel::class.java)
        itemClick = this
        initObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!NetworkUtils.isNetworkConnected(activity!!)) {
            baseshowFeedbackMessage(rootLayout, getString(R.string.no_internet))
        }else{
            progressBar.visibility = View.VISIBLE
            viewModel?.getTrending()
        }
    }

    fun initObserver() {

        viewModel?.trendingResponse?.observe(this, Observer {
            if (it?.response?.status.equals("1")) {
                progressBar.visibility = View.GONE
                setAdapter(it?.response!!.data)
            }else{
                progressBar.visibility=View.GONE
            }

        })
    }

    fun setAdapter(data: Data) {
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvTrending.layoutManager = linearLayoutManager
        rvTrending.isNestedScrollingEnabled = false
        mAdapter = TrendingParentAdapter(viewModel,data)
        rvTrending.adapter = mAdapter
    }
}