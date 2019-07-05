package com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class tableList (@SerializedName("data") var data: List<tableDTO>)