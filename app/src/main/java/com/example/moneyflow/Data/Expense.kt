package com.example.moneyflow.Data

data class Expense(
    val id: Int,
    val note: String?,
    val amount: Double,
    val category: CategoriaGasto,
    val date: Long
)
