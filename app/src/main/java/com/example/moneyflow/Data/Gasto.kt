package com.example.moneyflow.Data

data class Gasto(
    val id: Long = 0L,
    val monto: Double,
    val categoria: CategoriaGasto,
    val descripcion: String,
    val fecha: Long
)
