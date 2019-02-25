package com.zlobrynya.internshipzappa.tools.retrofit


import com.zlobrynya.internshipzappa.tools.retrofit.dto.DishList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface GetDishService {
    @GET
    fun getAllDishes(@Url url: String) : Call<DishList>
}