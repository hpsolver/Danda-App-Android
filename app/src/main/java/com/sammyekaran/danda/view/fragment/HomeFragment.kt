package com.sammyekaran.danda.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragmentWithoutBinding
import com.sammyekaran.danda.model.homefeed.HomeFeedModel
import com.sammyekaran.danda.model.homefeed.Result
import com.sammyekaran.danda.model.homefeed.Suggestion
import com.sammyekaran.danda.repositry.WebServices
import com.sammyekaran.danda.utils.*
import com.sammyekaran.danda.view.activity.ImageCropperActivity
import com.sammyekaran.danda.view.activity.StartActivity
import com.sammyekaran.danda.view.activity.StartActivity.Companion.REQUEST_CODE_UPLOAD
import com.sammyekaran.danda.view.activity.UploadActivity
import com.sammyekaran.danda.view.adapter.HomeAdapter
import com.sammyekaran.danda.view.adapter.SuggestionUserAdapter
import com.sammyekaran.danda.viewmodel.HomeViewModel
import im.ene.toro.widget.Container
import im.ene.toro.widget.PressablePlayerSelector
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragmentWithoutBinding(), LifecycleObserver {


    private var mMsgCountListner: ListenerRegistration? = null
    val REQUEST_TAKE_GALLERY_VIDEO = 101
    val STORAGE_PERMISSION_CODE = 102

    val REQUEST_CODE_CAMERA = 1001
    val REQUEST_CODE_GALLERY = 1002
    val REQUEST_CODE_CROP = 1003

    var mFeeddata: MutableList<Result> = mutableListOf()
    var suggestionList: MutableList<Suggestion> = mutableListOf()

    lateinit var layoutManager: LinearLayoutManager
    lateinit var mAdapter: HomeAdapter
    val viewModel: HomeViewModel by viewModel()
    lateinit var mSuggestionAdapter: SuggestionUserAdapter
    lateinit var filemanagerstring: String

    var mPageNo = 1
    var mCurentPage = 0
    var mLastPage = 0
    private var isLastPage = false
    private var isLoading = false
    val EVENT_GET_USER = "getUser"

    val sharedPref: SharedPref by inject()
    var respositry: WebServices = WebServices()
    lateinit var homeFeedModel: HomeFeedModel
    lateinit var mFeedRecyclerView: Container
    lateinit var selector: PressablePlayerSelector
    var isStop = false
    val db = FirebaseFirestore.getInstance()
    var mSelectedPostId = ""
    private lateinit var mAdView: AdView

    val permission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        if (!NetworkUtils.isNetworkConnected(activity!!)) {
            baseshowFeedbackMessage(rootLayoutHome, getString(R.string.no_internet))
        }
        findView(view)
        setAdapter(
                mFeeddata,
                suggestionList,
                sharedPref.getString(Constants.USER_ID))
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
                    sharedPref.getString(Constants.USER_ID),
                    mPageNo.toString(),
                    activity!!
            )
        })


    }

    private fun loadBannerAd() {
        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

        val testDeviceIds: List<String> = Arrays.asList("2E7CF230E53C9FA42F77B8A611DBB422")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        mAdView.loadAd(AdRequest.Builder().build())
    }

    private fun listener() {
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
        mAdView = view.findViewById(R.id.adView)
        selector = PressablePlayerSelector(mFeedRecyclerView)
        mFeedRecyclerView.playerSelector = selector

        loadBannerAd()
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
                baseshowFeedbackMessage(rootLayoutHome, message!!)
            }
        })


        viewModel.followUnfollow.observe(
                viewLifecycleOwner,
                Observer {
                    if (mFeeddata.size == 0) {
                        if (progressBar.visibility != View.VISIBLE) {
                            progressBar.visibility = View.VISIBLE
                        }
                        fetchFeed(
                                sharedPref.getString(Constants.USER_ID),
                                mPageNo.toString(),
                                activity!!
                        )
                        setAdapter(
                                mFeeddata,
                                suggestionList,
                                sharedPref.getString(Constants.USER_ID))
                    }
                })
    }

    fun setAdapter(
            data: MutableList<Result>,
            suggestionList: MutableList<Suggestion>,
            profileId: String
    ) {
        layoutManager = LinearLayoutManager(activity)
        mFeedRecyclerView.layoutManager = layoutManager
        mAdapter = HomeAdapter(data, suggestionList, viewModel, profileId, selector, activity, sharedPref.getString(Constants.FULL_NAME))
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
                                sharedPref.getString(Constants.USER_ID),
                                mPageNo.toString(),
                                activity!!
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
        fetchFeed(sharedPref.getString(Constants.USER_ID), mPageNo.toString(), activity!!)

    }


    private fun selectVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)
    }


    fun fetchFeed(userId: String, pageNo: String, activity: FragmentActivity) {
        respositry.fetchFeed(userId, pageNo, "A", mFCMToken, mDeviceId, mFCMToken, activity)
                .enqueue(object : Callback<HomeFeedModel> {
                    override fun onFailure(call: Call<HomeFeedModel>, t: Throwable) {
                        if (isStop)
                            return
                        progressBar.visibility = View.GONE
                        baseshowFeedbackMessage(rootLayoutHome, t.message!!)
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
                            homeFeedModel = response.body()!!
                            swipeRefresh.isRefreshing = false
                            if (homeFeedModel.response?.data?.notiCount != null && homeFeedModel.response?.data?.notiCount!! != "0") broadcastNotiCount(
                                    homeFeedModel.response?.data?.notiCount!!
                            )

                            if (homeFeedModel.response?.status.equals("0")) {
                                mFeeddata.clear()
                                baseshowFeedbackMessage(
                                        rootLayoutHome,
                                        homeFeedModel.response?.message!!
                                )
                                mAdapter.customNotify(
                                        mFeeddata,
                                        homeFeedModel.response?.data?.suggestions)

                            } else {
                                if (mPageNo == 1)
                                    mFeeddata.clear()

                                if (homeFeedModel.response?.data?.result?.size == 0) {
                                    //Welcome Text
                                    linearLayoutWelcome.visibility = View.VISIBLE
                                    //Set Suggestion Adapter
                                    if (homeFeedModel.response?.data?.suggestions!!.size > 0) {
                                        suggestionList.clear()
                                    }
                                    for (item in homeFeedModel.response?.data?.suggestions!!) {
                                        suggestionList.add(item)
                                    }

                                    layoutManager =
                                            LinearLayoutManager(
                                                    activity,
                                                    LinearLayoutManager.HORIZONTAL,
                                                    false
                                            )
                                    mFeedRecyclerView.layoutManager = layoutManager
                                    mSuggestionAdapter =
                                            SuggestionUserAdapter(suggestionList, viewModel)
                                    mFeedRecyclerView.adapter = mSuggestionAdapter

                                } else {

                                    linearLayoutWelcome.visibility = View.GONE
                                    mFeeddata = homeFeedModel.response?.data?.result!!
                                    if (homeFeedModel.response?.data?.suggestions!!.isNotEmpty()) {
                                        suggestionList.clear()
                                    }
                                    for (item in homeFeedModel.response?.data?.suggestions!!) {
                                        suggestionList.add(item)
                                    }

                                    mCurentPage = mPageNo
                                    mLastPage = homeFeedModel.response?.lastPage!!.toInt()
                                    if (mCurentPage != 1) mAdapter.removeLoading()
                                    mAdapter.customNotify(
                                            mFeeddata,
                                            suggestionList
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
                            }
                        } else {
                            baseshowFeedbackMessage(rootLayoutHome, "Server Error !")
                        }
                    }
                })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        if (mFeeddata.size == 0) {
            progressBar.visibility = View.VISIBLE
            fetchFeed(sharedPref.getString(Constants.USER_ID), mPageNo.toString(), activity!!)
        }
        /*GlobalScope.launch(Dispatchers.Main) {
            getUnreadMsgCount()
        }*/

    }

    @SuppressLint("SetTextI18n")
    /*private fun getUnreadMsgCount() {
        mMsgCountListner =
                db.collection("friendList").document(sharedPref.getString(Constants.USER_ID))
                        .collection("friends")
                        .whereGreaterThan("unReadCount", 0)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                return@addSnapshotListener
                            }
                            if (snapshot != null) {
                                var count = 0
                                for (i in snapshot.documents) count += i.get("unReadCount").toString()
                                        .toInt()
                                if (count > 0 && count <= 99) {
                                    tvUnreadCount.visibility = View.VISIBLE
                                    tvUnreadCount.text = count.toString()
                                } else if (count > 99) {
                                    tvUnreadCount.visibility = View.VISIBLE
                                    tvUnreadCount.text = getString(R.string.str_99_plus)
                                } else {
                                    tvUnreadCount.visibility = View.GONE
                                }
                            } else {
                                Log.d("TAG_HOME", "Current data: null")
                            }
                        }
    }*/

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {

            /* val display = activity!!.windowManager.defaultDisplay
             val size = Point()
             display.getSize(size)
             val width = size.x
             val height = size.y
             val rect = Rect(0, 0, width, height / 2)*/


            when (requestCode) {

                REQUEST_CODE_CAMERA -> {
                    /*CropImage.activity(data?.data)
                        .setAspectRatio(4, 3)
                        .setMinCropWindowSize(width, height / 2)
                        .setCropMenuCropButtonTitle("Ok")
                        .setOutputCompressQuality(80)
                        .start(activity!!)*/
                    startActivityForResult(Intent(activity, ImageCropperActivity::class.java)
                            .putExtra("filePath", BitmapUtils.getFilePathFromUri(activity!!, data?.data!!)), REQUEST_CODE_CROP)
                }

                REQUEST_CODE_GALLERY -> {
                    if (data?.extras?.getString("uri") != null) {
                        val arguments = Bundle().apply {
                            putString("path", data.extras?.getString("uri"))
                            putString("type", "I")
                        }
                        startActivityForResult(
                                Intent(activity, UploadActivity::class.java).putExtras(arguments),
                                StartActivity.REQUEST_CODE_UPLOAD
                        )

                    }
                }

                REQUEST_CODE_CROP -> {
                    if (data?.extras?.getString("uri") != null) {
                        val arguments = Bundle().apply {
                            putString("path", data.extras?.getString("uri"))
                            putString("type", "I")
                        }
                        startActivityForResult(
                                Intent(activity, UploadActivity::class.java).putExtras(arguments),
                                StartActivity.REQUEST_CODE_UPLOAD
                        )

                    }
                }

                REQUEST_TAKE_GALLERY_VIDEO -> {

                    val selectedImageUri = data?.data
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri!!.path!!

                    // MEDIA GALLERY
                    val selectedVideoPath = getPath(selectedImageUri)
                    if (selectedVideoPath != null) {
                        val arguments = Bundle().apply {
                            putString("path", selectedVideoPath)
                            putString("type", "V")
                        }

                        startActivityForResult(
                                Intent(activity, UploadActivity::class.java).putExtras(arguments),
                                StartActivity.REQUEST_CODE_UPLOAD
                        )
                    }
                }

                REQUEST_CODE_UPLOAD -> {
                    fetchFeed(
                            sharedPref.getString(Constants.USER_ID),
                            "0",
                            activity!!
                    )
                }
            }
        }
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
        activity!!.unregisterReceiver(onDownloadComplete)
        if (mMsgCountListner != null) {
            mMsgCountListner?.remove()
        }


    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            baseshowFeedbackMessage(rootLayoutHome, "Download Completed")
        }
    }
}
