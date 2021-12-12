package com.sammyekaran.danda.model.getcomment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response {

    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("total_records")
    @Expose
    var totalRecords: String? = null
    @SerializedName("last_page")
    @Expose
    var lastPage: String? = null
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

}