package com.sammyekaran.danda.model.resetResponse

import com.google.gson.annotations.SerializedName

data class Response (
		@SerializedName("status") val status : Int,
		@SerializedName("message") val message : String,
		@SerializedName("data") val data : Data
)