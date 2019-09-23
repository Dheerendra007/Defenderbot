package com.defenderbot.model.profile

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class EmergencyContactItem(

		@field:SerializedName("id")
		val id: Int? = null,

	@field:SerializedName("email_id")
	val emailId: String? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("relation")
	val relation: String? = null
)