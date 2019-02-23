package com.zlobrynya.internshipzappa.retrofit


import com.zlobrynya.internshipzappa.retrofit.dto.DishList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface GetDishService {
    @GET
    fun getAllDishes(@Url url: String) : Call<DishList>
}