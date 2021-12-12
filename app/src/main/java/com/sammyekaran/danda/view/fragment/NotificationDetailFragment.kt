package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentNotificationDetailBinding
import com.sammyekaran.danda.model.notification.NotificationModel
import com.sammyekaran.danda.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notification_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

class NotificationDetailFragment : BaseFragment<FragmentNotificationDetailBinding>() {


    lateinit var binding: FragmentNotificationDetailBinding
    val viewModel: NotificationViewModel by viewModel()
    lateinit var notification: NotificationModel
    val args: NotificationDetailFragmentArgs by navArgs()

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel

        textViewNoti.text=args.noti

    }


}