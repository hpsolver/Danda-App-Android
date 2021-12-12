package com.sammyekaran.danda.view.fragment

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.model.homefeed.HomeFeedModel
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.ImageZoomHelper
import com.sammyekaran.danda.utils.NetworkUtils
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.AllTrendingAdapter
import com.sammyekaran.danda.viewmodel.AllTrendingViewModel
import com.sammyekaran.danda.viewmodel.HomeViewModel
import im.ene.toro.widget.Container
import im.ene.toro.widget.PressablePlayerSelector
import kotlinx.android.synthetic.main.fragment_all_trending.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.progressBar
import kotlinx.android.synthetic.main.fragment_home.recyclerViewFeed
import kotlinx.android.synthetic.main.fragment_home.swipeRefresh
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllTrendingFragment : BaseFragmentWithoutBinding(), LifecycleObserver {


    var mFeeddata: MutableList<com.sammyekaran.danda.model.homefeed.Result> = mutableListOf()
    lateinit var layoutManager: LinearLayoutManager
    lateinit var mAdapter: AllTrendingAdapter
    val viewModel: AllTrendingViewModel by viewModel()

    var mPageNo = 1
    var mType = ""
    var mCurentPage = 0
    var mLastPage = 0
    private var isLastPage = false
    private var isLoading = false

    val sharedPref: SharedPref by inject()
    var respositry: WebServices = WebServices()
    lateinit var responseBean: HomeFeedModel
    lateinit var mFeedRecyclerView: Container
    lateinit var selector: PressablePlayerSelector
    var isStop = false
    val args: AllTrendingFragmentArgs by navArgs()


    override fun getLayoutId(): Int {
        return R.layout.fragment_all_trending
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mType=args.type

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        if (!NetworkUtils.isNetworkConnected(activity!!)) {
            baseshowFeedbackMessage(rootLayout, getString(R.string.no_internet))
        }
        findView(view)
        setAdapter(mFeeddata, sharedPref.getString(Constants.USER_ID))
        listener()
        setRecycleViewScrollListener()

        val navController: NavController = NavHostFragment.findNavController(this)
        // We use a String here, but any type that can be put in a Bundle is supported
        // We use a String here, but any type that can be put in a Bundle is supported
        val liveData: MutableLiveData<String> = navController.currentBackStackEntry!!
                .savedStateHandle
                .getLiveData("key")
        liveData.observe(viewLifecycleOwner, Observer {
            if (progressBar.visibility != View.VISIBLE) {
                progressBar.visibility = View.VISIBLE
            }
            fetchFeed(
                    mPageNo.toString(),
                    mType
            )
        })
    }

    private fun listener() {

        imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }

        swipeRefresh.setOnRefreshListener {
            ImageZoomHelper.setZoom(swipeRefresh, false)

            Handler().postDelayed({
                if (swipeRefresh != null) {
                    swipeRefresh.isRefreshing = false
                    ImageZoomHelper.setZoom(swipeRefresh, true)
                }
            }, 100)
            refresh()
        }
    }

    private fun findView(view: View) {
        mFeedRecyclerView = view.findViewById(R.id.recyclerViewFeed)
        selector = PressablePlayerSelector(mFeedRecyclerView)
        mFeedRecyclerView.playerSelector = selector


    }

    fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {

                if (isLoading) {
                    baseShowProgressBar("")
                } else {
                    baseHideProgressDialog()
                }
            }
        })

        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayout, message!!)
            }
        })

    }

    fun setAdapter(
            data: MutableList<com.sammyekaran.danda.model.homefeed.Result>,
            profileId: String
    ) {
        layoutManager = LinearLayoutManager(activity)
        mFeedRecyclerView.layoutManager = layoutManager
        mAdapter = AllTrendingAdapter(data,  viewModel, profileId, selector)
        mFeedRecyclerView.adapter = mAdapter

    }


    private fun setRecycleViewScrollListener() {
        recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                val totalItemCount = recyclerView.layoutManager!!.itemCount

                //Pagination
                if (dy > 0) {
                    if (mCurentPage < mLastPage && mCurentPage == mPageNo && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        mPageNo += 1
                        fetchFeed(
                                mPageNo.toString(),
                                mType
                        )
                    }
                }

            }

        })
    }


    private fun refresh() {
        swipeRefresh.isRefreshing = true
        mPageNo = 1
        isLastPage = false
        mAdapter.clear()
        if (progressBar.visibility != View.VISIBLE) {
            progressBar.visibility = View.VISIBLE
        }
        fetchFeed( mPageNo.toString(), mType)

    }


    fun fetchFeed( pageNo: String, type:String) {
        respositry.getAllTrending(pageNo, type)
                .enqueue(object : Callback<HomeFeedModel> {
                    override fun onFailure(call: Call<HomeFeedModel>, t: Throwable) {
                        if (isStop)
                            return
                        progressBar.visibility = View.GONE
                        baseshowFeedbackMessage(rootLayout, t.message!!)
                        Log.d("errrror", t.message!!)
                    }

                    override fun onResponse(
                            call: Call<HomeFeedModel>,
                            response: Response<HomeFeedModel>
                    ) {
                        if (isStop)
                            return

                        progressBar.visibility = View.GONE
                        if (response.body() != null) {
                            responseBean = response.body()!!
                            swipeRefresh.isRefreshing = false
                            if (responseBean.response?.data?.notiCount != null && responseBean.response?.data?.notiCount!! != "0") broadcastNotiCount(
                                    responseBean.response?.data?.notiCount!!
                            )

                            if (responseBean.response?.status.equals("0")) {
                                mFeeddata.clear()
                                baseshowFeedbackMessage(
                                        rootLayout,
                                        responseBean.response?.message!!
                                )
                                mAdapter.customNotify(
                                        mFeeddata)

                            } else {
                                if (mPageNo == 1)
                                    mFeeddata.clear()


                                    mFeeddata = responseBean.response?.data?.result!!

                                    mCurentPage = mPageNo
                                    mLastPage = responseBean.response?.lastPage!!.toInt()
                                    if (mCurentPage != 1) mAdapter.removeLoading()
                                    mAdapter.customNotify(
                                            mFeeddata
                                    )
                                    swipeRefresh.isRefreshing = false;
                                    // check weather is last page or not
                                    if (mCurentPage < mLastPage) {
                                        mAdapter.addLoading()
                                    } else {
                                        isLastPage = true
                                    }
                                    isLoading = false

                            }
                        } else {
                            baseshowFeedbackMessage(rootLayout, "Server Error !")
                        }
                    }
                })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        if (mFeeddata.size == 0) {
            progressBar.visibility = View.VISIBLE
            fetchFeed(mPageNo.toString(), mType)
        }

    }

    override fun onResume() {
        super.onResume()
        if (!hasPermission(Manifest.permission.CAMERA) && !hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    1
            )
        else if (!hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    1
            )
        else if (!hasPermission(Manifest.permission.CAMERA))
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 1)
    }


    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
                activity!!,
                permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onStart() {
        super.onStart()
        isStop = false
    }

    override fun onStop() {
        super.onStop()
        isStop = true
    }

    private fun broadcastNotiCount(
            count: String
    ) {
        val intent = Intent()
        intent.action = "com.appzorro.noti"
        intent.putExtra("count", count)
        activity?.sendBroadcast(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.unregisterReceiver(onDownloadComplete);

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            baseshowFeedbackMessage(rootLayout,"Download Completed")
        }
    }
}
