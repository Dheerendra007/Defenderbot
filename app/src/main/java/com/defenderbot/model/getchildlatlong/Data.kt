package com.defenderbot.model.getchildlatlong

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class Data(

	@field:SerializedName("last_online_status")
	val lastOnlineStatus: String? = null,

	@field:SerializedName("status_id")
	val statusId: Int? = null,

	@field:SerializedName("child_id")
	val childId: Int? = null,

	@field:SerializedName("battery_percent")
	val batteryPercent: Int? = null,

	@field:SerializedName("tracking_status")
	val trackingStatus: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("long")
	val jsonMemberLong: String? = null,

	@field:SerializedName("distance_from")
	val distanceFrom: String? = null,

	@field:SerializedName("image")
     val image: String? = null
)