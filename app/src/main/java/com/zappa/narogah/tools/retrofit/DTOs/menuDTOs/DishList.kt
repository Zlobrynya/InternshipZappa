package com.zappa.narogah.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName

data class DishList(@SerializedName("menu") var menu: List<DishDTO>)