package com.example.foodly.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        private var INSTANCE: RestaurantDatabase? = null
        fun getInstance(context: Context): RestaurantDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    RestaurantDatabase::class.java,
                    "restaurant-db")
                    .build()
            }
            return INSTANCE as RestaurantDatabase
        }
    }
}