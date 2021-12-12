package com.sammyekaran.danda.model.supportchat
import com.google.gson.annotations.SerializedName


data class DataItem(

	@field:SerializedName("flag")
	val flag: String? = null,

	@field:SerializedName("query_id")
	val queryId: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)