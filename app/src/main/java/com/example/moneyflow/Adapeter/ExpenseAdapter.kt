package com.example.moneyflow.Adapeter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Data.Expense
import com.example.moneyflow.R
import com.example.moneyflow.utils.DateUtils

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var list = listOf<Expense>()

    fun submitList(newList: List<Expense>) {
        list = newList
        notifyDataSetChanged()
    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val viewCategoryColor: View = view.findViewById(R.id.viewCategoryColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gasto, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = list[position]

        holder.tvCategoria.text = expense.category.displayName
        holder.tvMonto.text = "$${String.format("%.2f", expense.amount)}"

        // 🎨 Color por categoría
        holder.viewCategoryColor.setBackgroundColor(
            holder.itemView.context.getColor(expense.category.colorRes)
        )

        // 📅 Fecha (simple por ahora)
        holder.tvFecha.text = DateUtils.formatExpenseDate(expense.date)
    }


    override fun getItemCount(): Int = list.size
}
