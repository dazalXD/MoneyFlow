package com.example.moneyflow.Data.repository

import com.example.moneyflow.Data.dao.ExpenseDao
import com.example.moneyflow.Data.entity.ExpenseEntity
import kotlinx.coroutines.flow.map

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {
    // se definen las reglas de negocio que podemos trabajar.
    fun getExpenses() = expenseDao.getAllExpenses()

    // Usamos .map para manejar el valor dentro del Flow
    fun getTotalExpenses() = expenseDao.getTotalExpenses().map { total -> total ?: 0.0 }

    // agregar gasto
    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }
}
