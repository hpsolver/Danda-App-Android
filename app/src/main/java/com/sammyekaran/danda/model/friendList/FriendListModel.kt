package com.sammyekaran.danda.model.friendList

import com.google.firebase.firestore.DocumentReference
import java.util.*

class  FriendListModel {
    var reference: DocumentReference?=null
    var timeStamp: Date?=null
    var unReadCount:Int?=null
}