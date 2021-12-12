package com.sammyekaran.danda.model.reportReason
import com.google.gson.annotations.SerializedName
data class DataItem(

	@field:SerializedName("reason")
	val reason: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)