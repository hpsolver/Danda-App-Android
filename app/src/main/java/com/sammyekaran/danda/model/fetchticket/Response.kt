package com.sammyekaran.danda.model.fetchticket

data class Response(
	val data: List<DataItem>? = null,
	val message: String? = null,
	val status: String? = null
)
