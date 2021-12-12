package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentNotificationBinding
import com.sammyekaran.danda.model.notification.Datum
import com.sammyekaran.danda.model.notification.NotificationModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.NotificationAdapter
import com.sammyekaran.danda.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.adView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.recyclerView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {

    private var mAdapter: NotificationAdapter? = null
    lateinit var binding: FragmentNotificationBinding
    val viewModel: NotificationViewModel by viewModel()
    val sharedPref: SharedPref by inject()
    var data: MutableList<Datum> = mutableListOf<Datum>()
    var mPageNo = 1
    var mLastPage = 0
    var mCurentPage = 0
    lateinit var layoutManager: LinearLayoutManager
    var isRefresh = false
    var respositry: WebServices
    lateinit var notification: NotificationModel

    init {
        this.respositry = WebServices()
    }

    companion object {
        var isApiHit = false
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPageNo = 1
        mLastPage = 0
        data.clear()
        getNotification()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        setAdapter(data)
        setRecyclerViewScrollListener()
    }

    private fun getNotification() {
        getNotification(sharedPref.getString(Constants.USER_ID), mPageNo.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        loadBannerAd()
    }

    private fun loadBannerAd() {
        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

        val testDeviceIds: List<String> = Arrays.asList("2E7CF230E53C9FA42F77B8A611DBB422")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        adView.loadAd(AdRequest.Builder().build())
    }


    private fun initObserver() {

        viewModel.errorListener!!.observe(viewLifecycleOwner, Observer<String> { message -> baseshowFeedbackMessage(rootLayoutNoti, message!!) })


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

    fun refreshOnSwap() {
        swapRefresh.isRefreshing = true
        isRefresh = true
        mPageNo = 1
        data.clear()
        getNotification(sharedPref.getString(Constants.USER_ID), mPageNo.toString())


    }

    fun getNotification(userId: String, pageNo: String) {
        if (!isRefresh)
            baseShowProgressBar("")
        respositry.getNotification(userId, pageNo, activity).enqueue(object : Callback<NotificationModel> {
            override fun onFailure(call: Call<NotificationModel>, t: Throwable) {
                if (!isRefresh)
                    baseHideProgressDialog()
            }

            override fun onResponse(call: Call<NotificationModel>, response: Response<NotificationModel>) {
                if (recyclerView == null)
                    return
                notification = response.body()!!
                if (!isRefresh)
                    baseHideProgressDialog()
                if (isRefresh) {
                    if (swapRefresh == null)
                        return
                    swapRefresh.isRefreshing = false
                    isRefresh = false
                }

                if (notification.response?.status.equals("0")) {
                    //baseshowFeedbackMessage(rootLayoutNoti, notification.response?.message!!)
                } else {
                    mCurentPage = mPageNo
                    for (item in notification.response?.data!!) {
                        data.add(item)
                    }
                    mLastPage = notification.response?.lastPage!!.toInt()
                    if (mAdapter != null)
                        mAdapter!!.customNotify(data)
                    else
                        setAdapter(data)
                }

            }
        })
    }

    private fun setAdapter(data: List<Datum>?) {
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView?.isNestedScrollingEnabled = false
        mAdapter = NotificationAdapter(data, viewModel)
        recyclerView?.adapter = mAdapter
    }

    private fun setRecyclerViewScrollListener() {
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
                            getNotification(sharedPref.getString(Constants.USER_ID), mPageNo.toString())
                        }
                    }
                }

            }
        })
    }

}