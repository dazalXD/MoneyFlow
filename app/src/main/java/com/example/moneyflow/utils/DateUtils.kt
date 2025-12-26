package com.example.moneyflow.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatExpenseDate(timestamp: Long): String {
        val expenseCal = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val todayCal = Calendar.getInstance()

        return when {
            isSameDay(expenseCal, todayCal) -> "Hoy"
            isYesterday(expenseCal, todayCal) -> "Ayer"
            else -> {
                val formatter = SimpleDateFormat("dd MMM", Locale("es", "MX"))
                formatter.format(Date(timestamp))
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(expense: Calendar, today: Calendar): Boolean {
        today.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(expense, today)
    }
}
