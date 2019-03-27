package com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs

import com.google.gson.annotations.SerializedName
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.menuDTOs.DishDTO

data class DishList(@SerializedName("menu") var menu: List<DishDTO>)