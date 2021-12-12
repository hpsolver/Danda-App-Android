package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentBlockedUserBinding
import com.sammyekaran.danda.model.blockuserlist.BlockUserResponse
import com.sammyekaran.danda.model.blockuserlist.DataItem
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.BlockedUserAdapter
import com.sammyekaran.danda.viewmodel.BlockedViewModel
import kotlinx.android.synthetic.main.fragment_blocked_user.*
import kotlinx.android.synthetic.main.header_layout.*
import org.koin.android.ext.android.inject

class BlockedUserFragment : BaseFragment<FragmentBlockedUserBinding>() {

    lateinit var mAdapter: BlockedUserAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var binding: FragmentBlockedUserBinding
    var mBlockUserList: MutableList<DataItem> = mutableListOf()
    val viewModel: BlockedViewModel by inject()
    val sharedPref: SharedPref by inject()

    override fun getLayoutId(): Int {
        return R.layout.fragment_blocked_user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        tvTitle.text = resources.getString(R.string.blocked_user)
        listener()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        if (mBlockUserList.size == 0)
            viewModel.getBlockedAccounts(sharedPref.getString(Constants.USER_ID))
    }

    private fun listener() {
        imageViewBack.setOnClickListener { findNavController().popBackStack() }

    }

    private fun initObserver() {

        viewModel.errorListener!!.observe(viewLifecycleOwner, Observer<String> { message -> baseshowFeedbackMessage(rootLayout, message!!) })

        viewModel.blockUserResponse.observe(viewLifecycleOwner, Observer<BlockUserResponse> { it ->
            if (it?.response?.status.equals("0")) {
                tvNoContact.visibility=View.VISIBLE
                rvBlockedAccounts.visibility=View.GONE
            } else {
                tvNoContact.visibility=View.GONE
                rvBlockedAccounts.visibility=View.VISIBLE
                mBlockUserList.clear()
                mBlockUserList.addAll(it?.response?.data!!)
                setAdapter(mBlockUserList)
            }
        })


        viewModel.isLoading.observe(viewLifecycleOwner, Observer<Boolean> { isLoading ->
            if (isLoading) {
                baseShowProgressBar("")
            } else {
                baseHideProgressDialog()
            }
        })
    }

    private fun setAdapter(data: List<DataItem>?) {
        layoutManager = LinearLayoutManager(activity)
        rvBlockedAccounts.layoutManager = layoutManager
        rvBlockedAccounts?.isNestedScrollingEnabled = false
        mAdapter = BlockedUserAdapter(data, viewModel)
        rvBlockedAccounts?.adapter = mAdapter
    }

}