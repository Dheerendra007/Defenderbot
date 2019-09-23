package com.defenderbot.model.profile

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class Data(

	@field:SerializedName("email_id")
	val emailId: String? = null,

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("medical_condition")
	val medicalCondition: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("zip_code")
	val zipCode: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("platform_type")
	val platformType: String? = null,

	@field:SerializedName("pin")
	val pin: String? = null,

	@field:SerializedName("DOB")
	val dOB: String? = null,

	@field:SerializedName("blood_type")
	val bloodType: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("reset_otp")
	val resetOtp: Int? = null,

	@field:SerializedName("current_time")
	val currentTime: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("otp")
	val otp: Int? = null,

	@field:SerializedName("contact_number")
	val contactNumber: String? = null,

	@field:SerializedName("signup_type")
	val signupType: String? = null,

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("current_date")
	val currentDate: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("parent_id")
	val parentId: Int? = null,

	@field:SerializedName("device_token")
	val deviceToken: Any? = null,

	@field:SerializedName("current_status")
	val currentStatus: CurrentStatus? = null,

	@field:SerializedName("reset_status")
	val resetStatus: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("activated")
	val activated: Int? = null,


	@field:SerializedName("lat_long")
	val latLong: String? = null,


	@field:SerializedName("emergency_contact")
	val emergencyContact: List<EmergencyContactItem?>? = null
)