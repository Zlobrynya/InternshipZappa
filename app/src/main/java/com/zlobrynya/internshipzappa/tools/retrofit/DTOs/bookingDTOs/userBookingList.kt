package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs

import com.google.gson.annotations.SerializedName

data class userBookingList (@SerializedName("bookings") var bookings: List<userBookingDTO>)