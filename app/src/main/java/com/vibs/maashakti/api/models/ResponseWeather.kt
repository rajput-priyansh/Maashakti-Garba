package com.vibs.maashakti.api.models

import com.google.gson.annotations.SerializedName

data class ResponseCreateCard(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("status_code")
	val statusCode: Int? = null
)
