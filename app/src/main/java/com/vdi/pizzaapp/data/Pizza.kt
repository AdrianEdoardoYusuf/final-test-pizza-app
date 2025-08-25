package com.vdi.pizzaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pizza(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double
)
