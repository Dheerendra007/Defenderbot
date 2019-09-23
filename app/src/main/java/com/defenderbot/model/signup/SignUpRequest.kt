package com.defenderbot.model.signup

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class SignUpRequest(

		@field:SerializedName("email_id")
		var emailId: String? = null,

		@field:SerializedName("password")
		var password: String? = null,

		@field:SerializedName("username")
		var username: String? = null,

		@field:SerializedName("confirm_password")
		var confirmPassword: String? = null,

		@field:SerializedName("user_type")
		var usertype: String? = null,

		@field:SerializedName("parent_id")
		var parentid: Integer? = null,

		@field:SerializedName("profile_picture")
		var profilepicture: String? = null,

		@field:SerializedName("contact_number")
		var contactnumber: String? = null

)