package com.example.moneyflow.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.Data.CategoriaGasto
import com.example.moneyflow.Data.entity.ExpenseEntity
import com.example.moneyflow.Data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses = repository.getExpenses().asLiveData()

    val totalExpenses = repository.getTotalExpenses().asLiveData()

    fun addExpense(
        category: CategoriaGasto, amount: Double, note: String?
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    id = 0,
                    category = category,
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
}
