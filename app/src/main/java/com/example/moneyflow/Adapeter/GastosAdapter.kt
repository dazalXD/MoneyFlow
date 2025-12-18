package com.example.moneyflow.Adapeter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Data.Gasto
import com.example.moneyflow.R

class GastosAdapter(
    private val gastos: List<Gasto>,
    private val onItemClick: (Gasto) -> Unit
) : RecyclerView.Adapter<GastosAdapter.GastoViewHolder>() {
    class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: GastoViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}