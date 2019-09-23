package com.defenderbot.model.getpolyline

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class PolylineResponse(

	@field:SerializedName("add_long")
	val addLong: Double? = null,

	@field:SerializedName("childlocation")
	val childlocation: ArrayList<ChildlocationItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("add_lat")
	val addLat: Double? = null,

	@field:SerializedName("status")
	val status: Int? = null
)