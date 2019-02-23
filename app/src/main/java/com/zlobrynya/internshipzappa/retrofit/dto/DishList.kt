package com.zlobrynya.internshipzappa.retrofit.dto

import com.google.gson.annotations.SerializedName

data class DishList(@SerializedName("menu") var menu: List<DishDTO>) {
}