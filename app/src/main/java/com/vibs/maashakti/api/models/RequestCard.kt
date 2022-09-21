package com.vibs.maashakti.api.models

import com.google.gson.annotations.SerializedName

data class RequestCard(

	@field:SerializedName("user_nfc_no")
	val userNfcNo: String? = null,

	@field:SerializedName("registration_from")
	val registrationFrom: String? = "Manual2022",

	@field:SerializedName("last_name")
	val lastName: String? = "S",

	@field:SerializedName("middle_name")
	val middleName: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("secret_token")
	val secretToken: String? = "mashakti@admin",

	@field:SerializedName("user_contact_no")
	val userContactNo: String? = null,

	@field:SerializedName("user_photo_url")
	val userPhotoUrl: String? = null
)
