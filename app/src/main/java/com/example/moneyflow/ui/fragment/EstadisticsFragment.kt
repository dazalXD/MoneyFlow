package com.example.moneyflow.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.moneyflow.MoneyFlowApp
import com.example.moneyflow.R
import com.example.moneyflow.ViewModel.MainViewModel
import com.example.moneyflow.ViewModel.MainViewModelFactory

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import org.w3c.dom.Text
import kotlin.getValue

class EstadisticsFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((requireActivity().application as MoneyFlowApp).repository)
    }
    private lateinit var tvTotal: TextView
    private lateinit var pieChart: PieChart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estadistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initObserver()
//        val entries = listOf(
//            PieEntry(1f, "Comida"),
//            PieEntry(5f, "Transporte"),
//            PieEntry(8f, "Entretenimiento"),
//            PieEntry(10f, "Servicios")
//        )
//
//        val dataSet = PieDataSet(entries, "Gastos")
//        dataSet.colors = listOf(
//            Color.BLUE,
//            Color.GREEN,
//            Color.YELLOW,
//            Color.RED
//        )
//        dataSet.setDrawValues(true)
//        val pieData = PieData(dataSet)
//        pieChart.description.text = "Gastos"
//        pieChart.data = pieData
//        pieChart.invalidate()
    }

    fun initViews(view: View) {
        tvTotal = view.findViewById<TextView>(R.id.tvTotalStats)
        pieChart = view.findViewById<PieChart>(R.id.pieChart)
    }

    fun initObserver() {
        viewModel.totalExpenses.observe(viewLifecycleOwner) { total ->
            val displayTotal = total ?: 0.0
            tvTotal.text = "$${String.format("%.2f", displayTotal)}"
        }
    }
}