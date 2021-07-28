package com.example.foodly.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    suspend fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    suspend fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurant")
    suspend fun getAllRestaurant(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurant WHERE restaurant_Id =:restaurantId")
    suspend fun getAllRestaurant(restaurantId: String): RestaurantEntity
}