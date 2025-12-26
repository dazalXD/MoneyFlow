package com.example.moneyflow.ViewModel

import androidx.lifecycle.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moneyflow.Data.CategoriaGasto
import com.example.moneyflow.Data.Expense

class MainViewModel : ViewModel() {
    private val _expenses = MutableLiveData<MutableList<Expense>>(mutableListOf())
    val expenses: LiveData<MutableList<Expense>> get() = _expenses

    private val _totalExpenses = MutableLiveData(0.0)
    val totalExpenses: LiveData<Double> get() = _totalExpenses
    fun addExpense(
        category: CategoriaGasto,
        amount: Double,
        note: String?
    ) {
        val expense = Expense(
            id = 0,
            category = category,
            amount = amount,
            note = note,
            date = System.currentTimeMillis()
        )

        val currentList = _expenses.value ?: mutableListOf()
        currentList.add(expense)
        _expenses.value = currentList

        updateTotal()
    }


    private fun updateTotal() {
        _totalExpenses.value = _expenses.value?.sumOf { it.amount } ?: 0.0
    }
}