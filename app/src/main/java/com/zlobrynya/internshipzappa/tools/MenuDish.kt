package com.zlobrynya.internshipzappa.tools

import com.zlobrynya.internshipzappa.retrofit.dto.DishDTO
import com.zlobrynya.internshipzappa.tools.parcelable.Dish

class MenuDish {
    lateinit var hotArray: ArrayList<DishDTO>
    lateinit var saladsArray: ArrayList<DishDTO>
    lateinit var soupArray: ArrayList<DishDTO>
    lateinit var burgerArray: ArrayList<DishDTO>
    lateinit var nonalcArray: ArrayList<DishDTO>
    lateinit var beerArray: ArrayList<DishDTO>
    var connect = true
}