package com.example.moneyflow.Data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla de categorías para los gastos.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val colorHex: String,
    val isCustom: Boolean = false
)
