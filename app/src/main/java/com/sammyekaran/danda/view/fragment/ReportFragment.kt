package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentReportBinding
import com.sammyekaran.danda.model.ReportDetailModel
import com.sammyekaran.danda.model.reportReason.DataItem
import com.sammyekaran.danda.model.reportReason.ReportReasonesponse
import com.sammyekaran.danda.view.adapter.ReportAdapter
import com.sammyekaran.danda.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.header_layout.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReportFragment : BaseFragment<FragmentReportBinding>() , ReportAdapter.ItemClick{


    val viewModel: ReportViewModel by viewModel()
    lateinit var binding: FragmentReportBinding
    lateinit var layoutManager: LinearLayoutManager
    private lateinit var mAdapter: ReportAdapter
    val itemClick: ReportAdapter.ItemClick
    var mReportDetailList: MutableList<ReportDetailModel> = mutableListOf()
    val arg:ReportFragmentArgs by navArgs()

    init {
        this.itemClick=this
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_report
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        tvTitle.text = resources.getString(R.string.report)
        listner()

    }

    private fun listner() {
        imageViewBack.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        if (mReportDetailList.size == 0)
            viewModel.getReportList()
    }


    fun initObserver() {

        viewModel.reportReasonesponse.observe(viewLifecycleOwner, object : Observer<ReportReasonesponse> {
            override fun onChanged(data: ReportReasonesponse) {
                if (data.response?.status.equals("0")) {
                    baseshowFeedbackMessage(rootLayout, data.response?.message!!)
                } else {
                    createLocalModel(data.response?.data)
                    setAdapter(mReportDetailList)
                }

            }
        })


        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(rootLayout, message!!)
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

    }

    private fun createLocalModel(data: List<DataItem>?) {
        mReportDetailList.clear()
        for (item in data!!) {
            val detail = ReportDetailModel()
            when (item.id) {
                "1" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Just don't like the post ?"
                    detail.detail = "Unfollow the user so you won't see any photos and videos uploaded by that user."
                    mReportDetailList.add(detail)
                }
                "2" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Report against nudity or pornography ?"
                    detail.detail =
                        "We will remove: \n\n 1. Photo or video uploaded by user. \n\n 2. Post showing sexual intercouse. \n\n 3. Any nude or partially nude posts."
                    mReportDetailList.add(detail)
                }
                "3" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Report against hate speech or symbols ?"
                    detail.detail =
                        "We will remove: \n\n 1. Posts containing any kind of hate speech or any symbols. \n\n 2. Post showing any voilence or attack on others \n\n 3. post showing any kind of threats."
                    mReportDetailList.add(detail)
                }
                "4" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Report against violence or threat of violence ?"
                    detail.detail =
                        "We will remove: \n\n 1. Post showing any kind of violence. \n\n 2. Posts which encourage to attack someone against there religion or against gender biases. \n\n 3. Posts containing anykind of theft,physical harm or any financial harm."
                    mReportDetailList.add(detail)
                }
                "5" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Report against selling or buying of firearms ?"
                    detail.detail =
                        "We will remove: \n\n 1. Post showing any offers of buying or selling of the firearms. \n\n 2. Post showing any offers of buying or selling of any explosive material."
                    mReportDetailList.add(detail)
                }
                "6" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Report against selling or buying of drugs ?"
                    detail.detail =
                        "We will remove:\n\n 1. Posts showing the use of drugs and promoting drugs. \n\n 2. Posts showing any offers to buy or sell drugs"
                    mReportDetailList.add(detail)
                }
                "7" -> {
                    detail.id = item.id
                    detail.title = item.reason!!
                    detail.header = "Report against self injury ?"
                    detail.detail =
                        "We will remove: \n\n Posts showing or encoraging self injury like suicide,cutting themselves or eating substances causing death."
                    mReportDetailList.add(detail)
                }
            }
        }


    }

    private fun setAdapter(data: MutableList<ReportDetailModel>) {
        layoutManager = LinearLayoutManager(activity)
        rvReport.layoutManager = layoutManager
        rvReport?.isNestedScrollingEnabled = false
        mAdapter = ReportAdapter(data, viewModel, itemClick)
        rvReport?.adapter = mAdapter
    }

    override fun onClick(position: Int) {
       val  action=ReportFragmentDirections.actionReportFragmentToReportDetailFragment(mReportDetailList.get(position).id,arg.postId!!
       ,mReportDetailList.get(position).header,mReportDetailList.get(position).detail,arg.toUserId)
        findNavController().navigate(action)
    }
}