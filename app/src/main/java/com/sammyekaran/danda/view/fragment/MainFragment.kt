package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentMainBinding
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject


class MainFragment : BaseFragment<FragmentMainBinding>() {

    lateinit var mViewDatBinding: FragmentMainBinding
    val sharedPref: SharedPref by inject()
    private val commonUtils: CommonUtils by inject()

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDatBinding = getViewDataBinding()

        frameLayoutChat.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_friendListFragment) }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(TrendingFragment(), "Trending")
        viewpager.adapter = adapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.getTabAt(0)?.select()

    }
}