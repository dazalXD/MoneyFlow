package com.example.moneyflow.Data

import com.example.moneyflow.R

enum class CategoriaGasto (
    val displayName: String,
    val colorRes: Int
){
    COMIDA("Comida", R.color.category_food),
    TRANSPORTE("Transporte", R.color.category_transport),
    RENTA("Renta", R.color.category_rent),
    SERVICIOS("Servicios", R.color.category_services),
    ENTRETENIMIENTO("Entretenimiento", R.color.category_entertainment),
    SALUD("Salud", R.color.category_health),
    OTROS("Otros", R.color.category_other)
}