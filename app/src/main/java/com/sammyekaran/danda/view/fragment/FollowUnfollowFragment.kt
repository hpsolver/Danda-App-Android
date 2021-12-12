package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentFollowUnfollowBinding
import com.sammyekaran.danda.view.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_follow_unfollow.*

class FollowUnfollowFragment : BaseFragment<FragmentFollowUnfollowBinding>() {

    var mUsrerId = ""
    var mTotalFollower = ""
    var mTotalFollowing = ""
    val args: FollowUnfollowFragmentArgs by navArgs()


    override fun getLayoutId(): Int {
        return R.layout.fragment_follow_unfollow
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUsrerId = args.userID
        mTotalFollower = ProfileFragment.sTotalFolloers
        mTotalFollowing = ProfileFragment.sTotalFollowings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewBack.setOnClickListener {
            findNavController().popBackStack()

        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(FollowerFragment(mUsrerId), mTotalFollower + " " + "Follower")
        adapter.addFragment(FollowingFragment(mUsrerId), mTotalFollowing + " " + "Following")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        if (args.type.equals("1")) {
            tabLayout.getTabAt(0)?.select()
        } else {
            tabLayout.getTabAt(1)?.select()
        }

    }
}