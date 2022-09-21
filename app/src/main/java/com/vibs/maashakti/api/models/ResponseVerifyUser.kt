package com.vibs.maashakti.api.models

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ResponseVerifyUser(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("status_code")
	val statusCode: Int? = null,

	@field:SerializedName("participant_data")
	val participantData: ParticipantData? = null
):Parcelable


@Parcelize
data class ParticipantData(

	@field:SerializedName("Usr_Complimentary")
	val usrComplimentary: String? = null,

	@field:SerializedName("pass_id")
	val passId: String? = null,

	@field:SerializedName("user_nfc_no")
	val userNfcNo: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("registration_from")
	val registrationFrom: String? = null,

	@field:SerializedName("user_photo_url")
	val userPhotoUrl: String? = null,

	@field:SerializedName("user_contact_no")
	val userContactNo: String? = null,

	@field:SerializedName("gift_by")
	val giftBy: String? = null
):Parcelable
