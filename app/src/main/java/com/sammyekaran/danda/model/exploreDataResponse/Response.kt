package com.sammyekaran.danda.model.exploreDataResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response {
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("current_page")
    @Expose
    var currentPage: String? = null
    @SerializedName("page_size")
    @Expose
    var pageSize: String? = null
    @SerializedName("total_records")
    @Expose
    var totalRecords: String? = null
    @SerializedName("last_page")
    @Expose
    var lastPage: String? = null
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null
    @SerializedName("subscriptionType")
    @Expose
    var subscriptionType:String? = null

}