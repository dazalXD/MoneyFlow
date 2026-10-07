package com.example.moneyflow.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.Data.entity.CategoryEntity
import com.example.moneyflow.Data.entity.ExpenseEntity
import com.example.moneyflow.Data.repository.ExpenseRepository
import com.example.moneyflow.utils.DateUtils
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses = repository.getExpenses().asLiveData()
    val categories = repository.getCategories().asLiveData()

    val totalExpenses = repository.getTotalExpenses().asLiveData()
    val totalExpenseMes = repository.getTotalExpensesMesActual(
        DateUtils.getStartOfMonthTimestamp(),
        DateUtils.getEndOfMonthTimestamp()
    ).asLiveData()

    fun addExpense(
        categoryId: Int, amount: Double, note: String?
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    categoryId = categoryId,
                    amount = amount,
                    note = note,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun addCategory(name: String, colorHex: String, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.addCategory(
                CategoryEntity(
                    name = name,
                    colorHex = colorHex,
                    isCustom = true
                )
            )
            onComplete(id)
        }
    }
}
