package com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class visitingHoursList (@SerializedName("data") var data: List<visitingHoursDTO>)