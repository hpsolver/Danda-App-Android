package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentReportDetailBinding
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.model.reportReason.ReportReasonesponse
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.fragment_report_detail.*
import kotlinx.android.synthetic.main.header_layout.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ReportDetailFragment : BaseFragment<FragmentReportDetailBinding>() {

    val viewModel: ReportViewModel by viewModel()
    lateinit var binding:FragmentReportDetailBinding
    lateinit var layoutManager: LinearLayoutManager
    val args :ReportDetailFragmentArgs by navArgs()
    val sharedPref:SharedPref by inject()

    override fun getLayoutId(): Int {
        return R.layout.fragment_report_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=getViewDataBinding()
        binding.viewModel=viewModel
        tvTitle.text=resources.getString(R.string.report)
        tvHeader.text=args.header
        tvDetail.text=args.detail
        listner()

    }

    private fun listner() {
        imageViewBack.setOnClickListener { findNavController().popBackStack() }

        buttonReport.setOnClickListener {
            viewModel.addReportReasons(args.reportId,args.toUserId,args.postId,sharedPref.getString(Constants.USER_ID))
        }
        buttonBlock.setOnClickListener {
            viewModel.blockUnBlock(args.toUserId,sharedPref.getString(Constants.USER_ID),"1")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
    }


    fun initObserver() {

        viewModel.reportStatus!!.observe(viewLifecycleOwner, object : Observer<ReportReasonesponse> {
            override fun onChanged(data: ReportReasonesponse) {
               if (data.response?.status.equals("0")){

               }else{
                   findNavController().navigate(R.id.action_reportDetailFragment_to_homeFragment)
               }
            }
        })

        viewModel.blockUnblock!!.observe(viewLifecycleOwner, object : Observer<CommonModel> {
            override fun onChanged(data: CommonModel) {
                if (data.response?.status.equals("0")){

                }else{
                   findNavController().navigate(R.id.action_reportDetailFragment_to_homeFragment)
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

}