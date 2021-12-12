package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentSupportConversationBinding
import com.sammyekaran.danda.model.fetchsupportchat.DataItem
import com.sammyekaran.danda.model.fetchsupportchat.FetchChatResponse
import com.sammyekaran.danda.model.supportchat.SupportChatResponse
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.SupportConversationAdapter
import com.sammyekaran.danda.viewmodel.SupportViewModel
import kotlinx.android.synthetic.main.fragment_support_conversation.*
import kotlinx.android.synthetic.main.header_layout.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SupportConversationFragment : BaseFragment<FragmentSupportConversationBinding>() {

    lateinit var mAdapter: SupportConversationAdapter
    lateinit var binding: FragmentSupportConversationBinding
    val viewModel: SupportViewModel by viewModel()
    val sharedPref:SharedPref by inject()
    lateinit var layoutManager: LinearLayoutManager
    var conversationList: MutableList<DataItem> = mutableListOf()
    val args:SupportConversationFragmentArgs by navArgs()
    val commonUtils: CommonUtils by inject()
    override fun getLayoutId(): Int {
        return R.layout.fragment_support_conversation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        tvTitle.text= "# "+args.id
        setAdapter(conversationList)
        listner()
    }

    private fun listner() {
        imageViewBack.setOnClickListener { findNavController().popBackStack() }
        textViewPost.setOnClickListener {
            commonUtils.hideSoftKeyBoard(activity!!)
            if (!editTextComment.text?.isEmpty()!!){
                  viewModel.sendMsgToSupport(sharedPref.getString(Constants.USER_ID),args.id,editTextComment.text.toString())
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
        viewModel.fetchUserChat(args.id)
    }
     private fun initObserver() {
        viewModel.fetchChatResponse.observe(viewLifecycleOwner, object : Observer<FetchChatResponse> {
            override fun onChanged(data : FetchChatResponse) {
                if (data.response?.status.equals("0")) {
                } else {
                    conversationList.addAll(data.response?.data!!)
                    mAdapter.customNotify(conversationList)
                }

            }
        })

         viewModel.supportChatResponse.observe(viewLifecycleOwner, object : Observer<SupportChatResponse> {
            override fun onChanged(it : SupportChatResponse) {
                if (it.response?.status.equals("0")) {
                    baseshowFeedbackMessage(relativeLayoutRoot, it?.response?.message!!)
                } else {
                    editTextComment.setText("")
                    var data = DataItem()
                    data.message = it.response?.data?.get(0)?.message
                    data.created = it.response?.data?.get(0)?.created
                    data.flag = it.response?.data?.get(0)?.flag
                    conversationList.add(data)
                    mAdapter.customNotify(conversationList)
                }

            }
        })


        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                baseshowFeedbackMessage(relativeLayoutRoot, message!!)
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
        layoutManager.stackFromEnd=true
        rvSupportConversation.layoutManager = layoutManager
        rvSupportConversation?.isNestedScrollingEnabled = false
        mAdapter = SupportConversationAdapter(data, viewModel)
        rvSupportConversation?.adapter = mAdapter
    }
}