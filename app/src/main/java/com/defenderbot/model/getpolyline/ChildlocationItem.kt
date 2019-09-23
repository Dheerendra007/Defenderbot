package com.defenderbot.model.getpolyline

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class ChildlocationItem(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("destinationlong")
	val destinationlong: Double? = null,

	@field:SerializedName("originlat")
	val originlat: Double? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("destinationlat")
	val destinationlat: Double? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("originlong")
	val originlong: Double? = null,

	@field:SerializedName("destination_address")
    val destination_address: String? = ""
)