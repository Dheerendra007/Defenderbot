package com.defenderbot.model.login

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class Data(

	@field:SerializedName("email_id")
	val emailId: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("parent_id")
	val parentId: Int? = null,

	@field:SerializedName("reset_status")
	val resetStatus: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("image")
     val image: String? = null
)