package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentAddTicketBinding
import com.sammyekaran.danda.model.common.CommonModel
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.viewmodel.SupportViewModel
import kotlinx.android.synthetic.main.fragment_add_ticket.*
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.buttonSubmit
import kotlinx.android.synthetic.main.header_layout.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class AddTicketFragment : BaseFragment<FragmentAddTicketBinding>() {

    val viewModel:SupportViewModel by viewModel()
    lateinit var binding:FragmentAddTicketBinding
    val sharedPref:SharedPref by inject()
    val commonUtils: CommonUtils by inject()

    override fun getLayoutId(): Int {
        return R.layout.fragment_add_ticket
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=getViewDataBinding()
        binding.viewModel=viewModel
        tvTitle.text = activity?.getString(R.string.support)
        listner()

    }

    private fun listner() {
        imageViewBack.setOnClickListener { findNavController().popBackStack() }
        buttonSubmit.setOnClickListener {
            commonUtils.hideSoftKeyBoard(activity!!)
            binding.buttonSubmit.isEnabled = false
            viewModel.genrateTicket(sharedPref.getString(Constants.USER_ID), editTextQuery.text.toString()) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        viewModel.genrateTicket.observe(viewLifecycleOwner, object : Observer<CommonModel> {
            override fun onChanged(data: CommonModel?) {
                binding.buttonSubmit.isEnabled = true
                if (data?.response?.status.equals("0")) {
                    baseshowFeedbackMessage(rootLayoutChangePass, data?.response?.message!!)
                } else {
                    Toast.makeText(activity!!,data?.response?.message, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

            }
        })


        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                binding.buttonSubmit.isEnabled = true
                baseshowFeedbackMessage(linearLayoutTicket, message!!)
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
}