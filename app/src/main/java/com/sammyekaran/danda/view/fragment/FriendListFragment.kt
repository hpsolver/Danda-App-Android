package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentFriendListBinding
import com.sammyekaran.danda.model.friendList.FriendListModel
import com.sammyekaran.danda.model.friendList.UserDetail
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.FriendListAdapter
import com.sammyekaran.danda.viewmodel.FriendsViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_friend_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class FriendListFragment : BaseFragment<FragmentFriendListBinding>() {

    private var friendListModel: FriendListModel? = null
    lateinit var mAdapter: FriendListAdapter
    lateinit var layoutManager: LinearLayoutManager
    val sharedPref: SharedPref by inject()
    val commonUtils: CommonUtils by inject()
    lateinit var binding: FragmentFriendListBinding
    val viewModel: FriendsViewModel by viewModel()
    var friendList: MutableSet<UserDetail> = mutableSetOf()
    val db = FirebaseFirestore.getInstance()
    private var mGetFriendListListner: ListenerRegistration? = null
    var mTotalNoOfFriends = 0
    var isLoading = true


    override fun getLayoutId(): Int {
        return R.layout.fragment_friend_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        getFriendList()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        commonUtils.hideSoftKeyBoard(activity!!)
    }

    fun setAdapter(data: MutableSet<UserDetail>) {
        layoutManager = LinearLayoutManager(activity!!)
        recyclerView.layoutManager = layoutManager
        recyclerView?.isNestedScrollingEnabled = false
        mAdapter = FriendListAdapter(data, viewModel)
        recyclerView?.adapter = mAdapter

    }

    private fun getFriendList() {
        progressBar.visibility = View.VISIBLE
        val query = db.collection("friendList").document(sharedPref.getString(Constants.USER_ID)).collection("friends")
        mGetFriendListListner = query
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && isLoading) {
                        isLoading = false
                        mTotalNoOfFriends = snapshot.documents.size
                        friendList.clear()
                        if (snapshot.documents.size == 0) {
                            progressBar.visibility = View.GONE
                            textViewNoMessage.visibility = View.VISIBLE
                        }else {
                            textViewNoMessage.visibility = View.GONE
                        }
                        for (i in snapshot.documents) {
                            friendListModel = i.toObject(FriendListModel::class.java)
                            getFriendsDetail(
                                    friendListModel?.reference,
                                    friendListModel?.unReadCount,
                                    friendListModel?.timeStamp
                            )
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Log.d("TAG_HOME", "Current data: null")
                    }
                }


    }

    private fun getFriendsDetail(
            reference: DocumentReference?,
            unReadCount: Int?,
            timeStamp: Date?
    ) {

        reference?.get()?.addOnSuccessListener { documentSnapshot ->
            Log.d("REFERENCE_ID", reference.id)
            val userDetail = documentSnapshot.toObject(UserDetail::class.java)
            userDetail?.msgCount = unReadCount
            userDetail?.timestamp = timeStamp
            friendList.add(userDetail!!)
            if (mTotalNoOfFriends == friendList.size) {
                if (progressBar == null)
                    return@addOnSuccessListener
                progressBar.visibility = View.GONE
                //val sortedList = friendList.sortedWith(compareByDescending { it.timestamp })
                val sortedList = friendList.toSortedSet(compareByDescending { it.timestamp })
                setAdapter(sortedList)
                isLoading = true
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.errorListener?.removeObservers(this)
        mGetFriendListListner?.remove()
    }
}