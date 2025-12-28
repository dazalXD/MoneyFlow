package com.example.moneyflow

import android.app.Application
import androidx.room.Room
import com.example.moneyflow.Data.database.AppDatabase
import com.example.moneyflow.Data.repository.ExpenseRepository

class MoneyFlowApp : Application() {

    private val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "money_flow_db"
        ).build()
    }

    val repository by lazy {
        ExpenseRepository(database.expenseDao())
    }
}
