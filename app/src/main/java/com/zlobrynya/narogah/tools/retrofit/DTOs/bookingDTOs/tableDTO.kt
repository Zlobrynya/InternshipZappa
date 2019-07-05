package com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class tableDTO(
    @SerializedName("chair_count") var chair_count: Int = 0,
    @SerializedName("chair_type") var chair_type: String = "",
    @SerializedName("position") var position: String = "",
    @SerializedName("table_id") var table_id: Int = 0
)