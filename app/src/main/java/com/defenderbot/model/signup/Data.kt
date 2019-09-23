package com.defenderbot.model.signup

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class Data(

	@field:SerializedName("email_id")
	val emailId: String? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("parent_id")
	val parentId: Int? = null,

	@field:SerializedName("username")
	val username: String? = null
)