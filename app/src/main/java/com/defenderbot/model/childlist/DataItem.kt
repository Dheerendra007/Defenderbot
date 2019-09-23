package com.defenderbot.model.childlist

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class DataItem(

		@field:SerializedName("image")
	val image: String? = null,

		@field:SerializedName("user_id")
	val userId: Int? = null,

		@field:SerializedName("last_name")
	val lastName: String? = null,

		@field:SerializedName("first_name")
	val firstName: String? = null,
/*
		@field:SerializedName("destination_address")
		val destinationAddress: String? = null,*/


//		@field:SerializedName("lat")
//	val latchild: String? = null,
//
//		@field:SerializedName("long")
//	val longchild: String? = null,

		var is_selected: Boolean? = false



)