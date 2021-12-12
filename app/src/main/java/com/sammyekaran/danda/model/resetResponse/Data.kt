package com.sammyekaran.danda.model.resetResponse
import com.google.gson.annotations.SerializedName
data class Data (

		@SerializedName("email") val email : String,
		@SerializedName("password") val password : String
)