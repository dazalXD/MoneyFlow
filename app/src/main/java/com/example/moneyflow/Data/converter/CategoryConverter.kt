package com.example.moneyflow.Data.converter

import androidx.room.TypeConverter
import com.example.moneyflow.Data.CategoriaGasto

class CategoryConverter {

    @TypeConverter
    fun fromCategory(category: CategoriaGasto): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): CategoriaGasto {
        return CategoriaGasto.valueOf(value)
    }
}
