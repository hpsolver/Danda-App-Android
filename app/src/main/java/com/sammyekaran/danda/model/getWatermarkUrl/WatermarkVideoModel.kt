package com.sammyekaran.danda.model.getWatermarkUrl

import com.google.gson.annotations.SerializedName

data class WatermarkVideoModel(

	@field:SerializedName("response")
	val response: Response? = null
)

data class Response(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
