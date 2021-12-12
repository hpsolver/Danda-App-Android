package com.sammyekaran.danda.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentChatBinding
import com.sammyekaran.danda.model.MessageModel
import com.sammyekaran.danda.model.fcmnotification.Data
import com.sammyekaran.danda.model.fcmnotification.Notification
import com.sammyekaran.danda.model.fcmnotification.NotificationRequest
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.ChatAdapter
import com.sammyekaran.danda.viewmodel.FriendsViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.android.ext.android.inject
import java.util.*

class ChatFragment : BaseFragment<FragmentChatBinding>() {

    private var mUnReadCount: Int = 0
    private var mFcmToken: String? = null
    private var onlineObserver: ListenerRegistration? = null
    private var mMessageListner: ListenerRegistration? = null
    private var mMessageCountListner: ListenerRegistration? = null
    var message: MutableList<MessageModel> = mutableListOf<MessageModel>()
    private val TAG = "ChatFragment"
    val sharedPref: SharedPref by inject()
    var mReceiverId = "0"
    var mDeviceToken = "0"
    private var mUserName = ""
    val db = FirebaseFirestore.getInstance()
    var userMessagePath = ""
    var isOnline: Boolean? = null
    var mChatWithId: String? = null

    lateinit var binding: FragmentChatBinding
    val viewModel: FriendsViewModel by inject()
    var mMessageText = ""

    lateinit var layoutManager: LinearLayoutManager
    lateinit var mAdapter: ChatAdapter

    val DB_KEY_ALL_MSG = "AllMessages"
    val DB_KEY_MSG = "Messages"
    val DB_KEY_ALL_USER_INFO = "AllUsersInfo"
    val DB_KEY_FRIEND_LIST = "friendList"
    val DB_KEY_FRIENDS = "friends"

    val DB_FIELD_COUNT = "unReadCount"
    val DB_FIELD_ACTIVE = "active"
    val DB_FIELD_CHATWITH = "chatWith"
    val DB_FIELD_TIMESTAMP = "timeStamp"
    val DB_FIELD_REFERENCE = "reference"


    override fun getLayoutId(): Int {
        return R.layout.fragment_chat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFirebaseToken()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel

        val args: ChatFragmentArgs by navArgs()
        mReceiverId = args.userId
        mDeviceToken = args.fcmToken
        mUserName = args.userName

        if (mReceiverId.toInt() < sharedPref.getString(Constants.USER_ID).toInt()) {
            userMessagePath = mReceiverId + "-" + sharedPref.getString(Constants.USER_ID)
        } else {
            userMessagePath = sharedPref.getString(Constants.USER_ID) + "-" + mReceiverId
        }
        tvUserName.text = mUserName.capitalize()
        progressBar.visibility = View.VISIBLE
        setAdapter(message)
        listner()
        getLastCount()
        onlineListner()
        getLastMessage()
        setCountZero()

    }


    override fun onResume() {
        super.onResume()
        setOnline()
    }


    override fun onStop() {
        super.onStop()
        setOffline()
    }

    private fun setOnline() {
        if (sharedPref.getString(Constants.USER_ID).isNotEmpty()) {
            val userData: HashMap<String, Any> = hashMapOf(
                DB_FIELD_ACTIVE to true,
                DB_FIELD_CHATWITH to mReceiverId
            )
            FirebaseFirestore.getInstance().collection(DB_KEY_ALL_USER_INFO)
                .document(sharedPref.getString(Constants.USER_ID))
                .set(userData, SetOptions.merge())
        }
    }

    private fun setOffline() {

        if (sharedPref.getString(Constants.USER_ID).isNotEmpty()) {
            val userData: HashMap<String, Any> = hashMapOf(
                DB_FIELD_ACTIVE to false,
                DB_FIELD_CHATWITH to ""
            )
            FirebaseFirestore.getInstance().collection(DB_KEY_ALL_USER_INFO)
                .document(sharedPref.getString(Constants.USER_ID))
                .update(userData)
        }

    }

    private fun getLastCount() {
        val query = db.collection(DB_KEY_FRIEND_LIST).document(mReceiverId)
            .collection(DB_KEY_FRIENDS).document(sharedPref.getString(Constants.USER_ID))
        mMessageCountListner = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                mUnReadCount = snapshot.data?.get("unReadCount").toString().toInt()
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onlineListner() {
        val query = db.collection(DB_KEY_ALL_USER_INFO).document(mReceiverId)
        onlineObserver = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                if (snapshot.data?.get(DB_FIELD_ACTIVE) as Boolean) {
                    isOnline = true
                    ivOnline.setBackgroundResource(R.drawable.green_bubble)
                    tvOnlineStatus.text = getString(R.string.online)
                } else {
                    isOnline = false
                    tvOnlineStatus.text = getString(R.string.offline)
                    ivOnline.setBackgroundResource(R.drawable.red_bubble)
                }

                mChatWithId=snapshot.data?.get(DB_FIELD_CHATWITH).toString()

                Glide.with(activity!!)
                    .load(snapshot.data?.get("pic").toString().trim())
                    .placeholder(R.drawable.ic_icon_avatar)
                    .error(R.drawable.ic_icon_avatar)
                    .into(ivFriendPic)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }


    private fun listner() {
        imageViewBack.setOnClickListener { findNavController().popBackStack() }
        textViewSend.setOnClickListener {
            if (editTextMessage.text.toString().trim().length != 0) {
                sendMessage()
            }
        }

        ivFriendPic.setOnClickListener {
          findNavController().navigate(ChatFragmentDirections.actionChatFragmentToProfileFragment(mReceiverId)) }
        llUserName.setOnClickListener {
            ivFriendPic.performClick() }
    }

    private fun sendMessage() {
        mMessageText = editTextMessage.text.toString()
        editTextMessage.setText("")
        if (message.size == 0) {
            // [START add friend]

            val senedrData = hashMapOf(
                DB_FIELD_REFERENCE to db.document("/" + DB_KEY_ALL_USER_INFO + "/" + mReceiverId),
                DB_FIELD_TIMESTAMP to FieldValue.serverTimestamp(),
                DB_FIELD_COUNT to 0

            )
            FirebaseFirestore.getInstance().collection(DB_KEY_FRIEND_LIST)
                .document(sharedPref.getString(Constants.USER_ID))
                .collection(DB_KEY_FRIENDS).document(mReceiverId)
                .set(senedrData)


            val receiverData = hashMapOf(
                DB_FIELD_TIMESTAMP to FieldValue.serverTimestamp(),
                DB_FIELD_COUNT to 0,
                DB_FIELD_REFERENCE to db.document("/" + DB_KEY_ALL_USER_INFO + "/" + sharedPref.getString(Constants.USER_ID))
            )
            FirebaseFirestore.getInstance().collection(DB_KEY_FRIEND_LIST).document(mReceiverId)
                .collection(DB_KEY_FRIENDS).document(sharedPref.getString(Constants.USER_ID))
                .set(receiverData)
        }
        // [STOP add friend method]

        val message = hashMapOf(
            "fromUser" to sharedPref.getString(Constants.USER_ID),
            "mTime" to System.currentTimeMillis(),
            "message" to mMessageText
        )

        db.collection(DB_KEY_ALL_MSG).document(userMessagePath).collection(DB_KEY_MSG)
            .add(message)
            .addOnSuccessListener { documentReference ->
                if (mChatWithId!=null&&!mChatWithId.equals(sharedPref.getString(Constants.USER_ID)))
                    senNotification(mMessageText)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


        // [START update time and read count]

        val senedrData: HashMap<String, Any> = hashMapOf(
            DB_FIELD_TIMESTAMP to FieldValue.serverTimestamp(),
            DB_FIELD_COUNT to 0

        )
        FirebaseFirestore.getInstance().collection(DB_KEY_FRIEND_LIST).document(sharedPref.getString(Constants.USER_ID))
            .collection(DB_KEY_FRIENDS).document(mReceiverId)
            .update(senedrData)

        if (mChatWithId!=null&&mChatWithId.equals(sharedPref.getString(Constants.USER_ID))) {
            val receiverData: HashMap<String, Any> = hashMapOf(
                DB_FIELD_TIMESTAMP to FieldValue.serverTimestamp()
            )
            updateCountAndTime(receiverData)
        } else {
            val receiverData: HashMap<String, Any> = hashMapOf(
                DB_FIELD_TIMESTAMP to FieldValue.serverTimestamp(),
                DB_FIELD_COUNT to mUnReadCount.plus(1)
            )
            updateCountAndTime(receiverData)
        }
        // [STOP update time and read count]
    }

    private fun updateCountAndTime(receiverData: HashMap<String, Any>) {
        FirebaseFirestore.getInstance().collection(DB_KEY_FRIEND_LIST).document(mReceiverId)
            .collection(DB_KEY_FRIENDS).document(sharedPref.getString(Constants.USER_ID))
            .update(receiverData)
    }

    private fun senNotification(message: String) {
        val key = Constants.LEGENCY_SERVER_KEY
        val request = NotificationRequest()
        val notification = Notification()
        val data = Data()
        data.body =   message
        data.title = sharedPref.getString(Constants.FULL_NAME)
        data.fromUser = sharedPref.getString(Constants.USER_ID)
        data.fromUserToken = mFcmToken
        data.fromUserName = sharedPref.getString(Constants.FULL_NAME)
        data.notiType = "firebaseMsg"
        data.type = "firebaseMsg"
        data.sound = "default"
        notification.body =  message
        notification.title = sharedPref.getString(Constants.FULL_NAME)
        request.collapseKey = "type_a"
        request.data = data
        request.notification = notification
        request.to = mDeviceToken
        viewModel.senNotification(key, request)

    }

    private fun getLastMessage() {


        mMessageListner = db.collection(DB_KEY_ALL_MSG).document(userMessagePath).collection(DB_KEY_MSG)
            .orderBy("mTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots!!.isEmpty) {
                    if (progressBar.visibility == View.VISIBLE) {
                        progressBar.visibility = View.GONE
                    }
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            if (progressBar.visibility == View.VISIBLE) {
                                progressBar.visibility = View.GONE
                            }
                            message = snapshots.toObjects(MessageModel::class.java)
                            mAdapter.customNotify(message)
                            recyclerView.adapter?.itemCount?.minus(1)?.let { recyclerView.smoothScrollToPosition(it) }
                        }
                    }
                }

            }


    }

    private fun setCountZero() {
        db.collection(DB_KEY_FRIEND_LIST).document(sharedPref.getString(Constants.USER_ID))
            .collection(DB_KEY_FRIENDS).document(mReceiverId).update(DB_FIELD_COUNT, 0)

    }

    fun setAdapter(
        data: MutableList<MessageModel>
    ) {
        layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        mAdapter = ChatAdapter(data, sharedPref.getString(Constants.USER_ID))
        recyclerView?.adapter = mAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMessageListner?.remove()
        onlineObserver?.remove()
        mMessageCountListner?.remove()
    }

    private fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult> {
                override fun onComplete(task: Task<InstanceIdResult>) {
                    if (!task.isSuccessful) {
                        return
                    }
                    // Get new Instance ID token
                    mFcmToken = task.result?.token
                }

            })
    }


}