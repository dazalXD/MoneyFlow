package com.example.moneyflow.Data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moneyflow.Data.dao.CategoryDao
import com.example.moneyflow.Data.dao.ExpenseDao
import com.example.moneyflow.Data.entity.CategoryEntity
import com.example.moneyflow.Data.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
}
