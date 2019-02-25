package com.zlobrynya.internshipzappa.tools.retrofit.dto

import com.google.gson.annotations.SerializedName

data class DishList(@SerializedName("menu") var menu: List<DishDTO>) {
}