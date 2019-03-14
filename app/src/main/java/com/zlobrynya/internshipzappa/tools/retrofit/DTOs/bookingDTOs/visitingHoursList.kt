package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class visitingHoursList (@SerializedName("data") var data: List<visitingHoursDTO>)