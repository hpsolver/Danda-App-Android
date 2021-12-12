package com.sammyekaran.danda.model.resetResponse
import com.google.gson.annotations.SerializedName


data class ResetPasswordResponse (
	@SerializedName("response") val response : Response
)