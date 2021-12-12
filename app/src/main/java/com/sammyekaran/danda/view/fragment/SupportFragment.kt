package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentSupportBinding
import com.sammyekaran.danda.model.fetchticket.DataItem
import com.sammyekaran.danda.model.fetchticket.FetchTicketsResponse
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.TicketAdapter
import com.sammyekaran.danda.viewmodel.SupportViewModel
import kotlinx.android.synthetic.main.fragment_support.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SupportFragment : BaseFragment<FragmentSupportBinding>() {

    lateinit var mAdapter: TicketAdapter
    lateinit var binding: FragmentSupportBinding
    val viewModel: SupportViewModel by viewModel()
    val sharedPref:SharedPref by inject()
    lateinit var layoutManager: LinearLayoutManager
    var data: MutableList<DataItem> = mutableListOf()
    override fun getLayoutId(): Int {
        return R.layout.fragment_support
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        setAdapter(data)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        viewModel.fetchTickets(sharedPref.getString(Constants.USER_ID))
    }
     private fun initObserver() {
        viewModel.fetchTicketsResponse.observe(viewLifecycleOwner, object : Observer<FetchTicketsResponse> {
            override fun onChanged(data : FetchTicketsResponse) {
                if (data.response?.status.equals("0")) {
                    tvStartConversation.visibility=View.VISIBLE
                    rvTickets.visibility=View.GONE
                } else {
                    tvStartConversation.visibility=View.GONE
                    rvTickets.visibility=View.VISIBLE
                    mAdapter.customNotify(data.response!!.data)
                }

            }
        })


        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(linearLayoutSupport, message!!)
            }
        })


        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("Wait...")
                } else {
                    baseHideProgressDialog()
                }
            }
        })
    }

    private fun setAdapter(data: MutableList<DataItem>) {
        layoutManager = LinearLayoutManager(activity)
        rvTickets.layoutManager = layoutManager
        rvTickets?.isNestedScrollingEnabled = false
        mAdapter = TicketAdapter(data, viewModel)
        rvTickets?.adapter = mAdapter
    }
}