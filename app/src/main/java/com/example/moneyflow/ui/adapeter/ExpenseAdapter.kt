package com.example.moneyflow.ui.adapeter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Data.entity.ExpenseWithCategory
import com.example.moneyflow.R
import com.example.moneyflow.utils.DateUtils

class ExpenseAdapter(
    private val onLongClick: (ExpenseWithCategory) -> Unit,
    private val onClick: (ExpenseWithCategory) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var list = listOf<ExpenseWithCategory>()

    fun submitList(newList: List<ExpenseWithCategory>?) {
        list = newList ?: listOf()
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
        val item = list[position]
        val expense = item.expense
        val category = item.category

        holder.tvCategoria.text = category.name
        holder.tvMonto.text = "$${String.format("%.2f", expense.amount)}"

        // 🎨 Color dinámico desde Hex
        val categoryColor = try {
            Color.parseColor(category.colorHex)
        } catch (e: Exception) {
            Color.GRAY
        }
        holder.viewCategoryColor.setBackgroundColor(categoryColor)

        // 📅 Fecha formateada
        holder.tvFecha.text = DateUtils.formatExpenseDate(expense.date)

        // 👆 Long Click para eliminar
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }

        // 👇 Click para ver el detalle
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = list.size
}
