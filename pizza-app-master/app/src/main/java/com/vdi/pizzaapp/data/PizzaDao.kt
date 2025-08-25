package com.vdi.pizzaapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PizzaDao {
    @Query("SELECT * FROM Pizza ORDER BY id DESC")
    fun getAll(): Flow<List<Pizza>>

    @Insert
    suspend fun insert(pizza: Pizza)

    @Update
    suspend fun update(pizza: Pizza)

    @Delete
    suspend fun delete(pizza: Pizza)
}
