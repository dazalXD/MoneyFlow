package com.example.moneyflow.Data.repository

import com.example.moneyflow.Data.dao.CategoryDao
import com.example.moneyflow.Data.dao.ExpenseDao
import com.example.moneyflow.Data.entity.CategoryEntity
import com.example.moneyflow.Data.entity.ExpenseEntity
import com.example.moneyflow.Data.entity.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {
    // Obtener gastos con sus categorías
    fun getExpenses(): Flow<List<ExpenseWithCategory>> = expenseDao.getAllExpenses()

    // Obtener lista de categorías. Si está vacía, sembrar las categorías por defecto automáticamente
    fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories().onEach { list ->
        if (list.isEmpty()) {
            val defaultCategories = listOf(
                CategoryEntity(name = "Comida", colorHex = "#FF7043"),
                CategoryEntity(name = "Transporte", colorHex = "#2196F3"),
                CategoryEntity(name = "Renta", colorHex = "#9C27B0"),
                CategoryEntity(name = "Servicios", colorHex = "#FF9800"),
                CategoryEntity(name = "Entretenimiento", colorHex = "#E91E63"),
                CategoryEntity(name = "Salud", colorHex = "#F44336"),
                CategoryEntity(name = "Otros", colorHex = "#607D8B")
            )
            categoryDao.insertCategories(defaultCategories)
        }
    }

    // Total general de gastos
    fun getTotalExpenses(): Flow<Double> = expenseDao.getTotalExpenses().map { total ->
        total ?: 0.0
    }

    // Total del mes actual
    fun getTotalExpensesMesActual(startDate: Long, endDate: Long): Flow<Double> =
        expenseDao.getTotalExpensesMes(startDate, endDate).map { total ->
            total ?: 0.0
        }

    // Agregar gasto
    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    // Eliminar gasto
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    // Actualizar gasto
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    // Agregar categoría personalizada
    suspend fun addCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }
}
