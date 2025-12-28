package com.example.moneyflow.Data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moneyflow.Data.CategoriaGasto

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: CategoriaGasto,
    val amount: Double,
    val note: String?,
    val date: Long
)