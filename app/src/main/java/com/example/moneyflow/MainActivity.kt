package com.example.moneyflow

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Adapeter.ExpenseAdapter
import com.example.moneyflow.Data.CategoriaGasto
import com.example.moneyflow.ViewModel.MainViewModel
import com.example.moneyflow.utils.DialogHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var rvGastos: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views
        rvGastos = findViewById(R.id.rvGastos)
        tvTotal = findViewById(R.id.tvTotal)
        fabAdd = findViewById(R.id.fabAdd)

        // RecyclerView
        adapter = ExpenseAdapter()
        rvGastos.layoutManager = LinearLayoutManager(this)
        rvGastos.adapter = adapter

        // Observar cambios en gastos
        viewModel.expenses.observe(this) { list ->
            adapter.submitList(list.toList())
        }
        viewModel.totalExpenses.observe(this) { total ->
            tvTotal.text = "$${String.format("%.2f", total)}"
        }

        // FAB para agregar gasto
        fabAdd.setOnClickListener {
            showAddExpenseDialog()
        }

        // Salir de la app con alerta
        onBackPressedDispatcher.addCallback(this) {
            showExitDialog()
        }
    }

    private fun showAddExpenseDialog() {
        // Usamos un AlertDialog simple para ingresar gasto
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Agregar Gasto")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)

        val categorySpinner = Spinner(this)
        val categories = CategoriaGasto.values().map { it.displayName }
        categorySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        layout.addView(categorySpinner)

        val noteEdit = EditText(this)
        noteEdit.hint = "Nota (opcional)"
        layout.addView(noteEdit)

//        val descEdit = EditText(this)
//        descEdit.hint = "Descripción"
//        layout.addView(descEdit)

        val amountEdit = EditText(this)
        amountEdit.hint = "Monto"
        amountEdit.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(amountEdit)

        builder.setView(layout)

        builder.setPositiveButton("Agregar") { _, _ ->
            val amount = amountEdit.text.toString().toDoubleOrNull()
            val selectedCategory = CategoriaGasto.values()[categorySpinner.selectedItemPosition]
            val note = noteEdit.text.toString()

            if (amount == null) {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            viewModel.addExpense(
                category = selectedCategory,
                amount = amount,
                note = note
            )
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showExitDialog() {
        DialogHelper.showStandardAlert(
            context = this,
            title = "Salir de la app",
            message = "¿Estás seguro de que deseas salir?",
            positiveButtonText = "Sí",
            onPositiveClick = {
                finish()
            },
            negativeButtonText = "No"
        )
    }
}
