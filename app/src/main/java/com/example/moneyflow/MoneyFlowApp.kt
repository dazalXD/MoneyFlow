package com.example.moneyflow

import android.app.Application
import androidx.room.Room
import com.example.moneyflow.Data.database.AppDatabase
import com.example.moneyflow.Data.repository.ExpenseRepository

class MoneyFlowApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "money_flow_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    val repository: ExpenseRepository by lazy {
        ExpenseRepository(database.expenseDao(), database.categoryDao())
    }
}
