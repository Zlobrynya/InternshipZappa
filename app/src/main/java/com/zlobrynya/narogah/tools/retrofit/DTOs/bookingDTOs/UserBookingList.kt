package com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class UserBookingList (@SerializedName("bookings") var bookings: List<UserBookingDTO>)