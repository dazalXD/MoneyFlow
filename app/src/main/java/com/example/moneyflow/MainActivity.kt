package com.example.moneyflow

import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
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
import com.example.moneyflow.ViewModel.MainViewModelFactory
import com.example.moneyflow.utils.DialogHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as MoneyFlowApp).repository)
    }

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

        // RecyclerView con funcionalidad de click largo para eliminar
        adapter = ExpenseAdapter(onLongClick = { expense ->
            showDeleteConfirmation(expense)
        }, onClick = { expense ->
            showExpenseDetails(expense)
        })

        rvGastos.layoutManager = LinearLayoutManager(this)
        rvGastos.adapter = adapter

        // Observar cambios en gastos
        viewModel.expenses.observe(this) { list ->
            adapter.submitList(list)
        }

        viewModel.totalExpenses.observe(this) { total ->
            val displayTotal = total ?: 0.0
            tvTotal.text = "$${String.format("%.2f", displayTotal)}"
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

    private fun showDeleteConfirmation(expense: com.example.moneyflow.Data.entity.ExpenseEntity) {
        DialogHelper.showStandardAlert(
            context = this,
            title = "Eliminar gasto",
            message = "¿Estás seguro de que deseas eliminar este gasto de $${expense.amount}?",
            positiveButtonText = "Eliminar",
            onPositiveClick = {
                viewModel.deleteExpense(expense)
                Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show()
            },
            negativeButtonText = "Cancelar"
        )
    }

    private fun showExpenseDetails(expense: com.example.moneyflow.Data.entity.ExpenseEntity) {
        DialogHelper.showStandardAlert(
            context = this, title = "Detalles del gasto", message = """
                Categoría: ${expense.category.displayName}
                Monto: $${expense.amount}
                Nota: ${expense.note ?: "Sin nota"}
                """.trimIndent(), positiveButtonText = "Cerrar"
        )
    }

    private fun showAddExpenseDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Agregar Gasto")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val categorySpinner = Spinner(this)
        val categories = CategoriaGasto.values().map { it.displayName }
        categorySpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )
        layout.addView(categorySpinner)

        val amountEdit = EditText(this)
        amountEdit.hint = "Monto"
        amountEdit.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(amountEdit)

        val maxLengh = 30
        val noteEdit = EditText(this)
        noteEdit.hint = "Nota (opcional)"
        noteEdit.filters = arrayOf(InputFilter.LengthFilter(maxLengh))
        layout.addView(noteEdit)

        builder.setView(layout)

        builder.setPositiveButton("Agregar") { _, _ ->
            val amountStr = amountEdit.text.toString()
            val amount = amountStr.toDoubleOrNull()
            val selectedCategory = CategoriaGasto.values()[categorySpinner.selectedItemPosition]
            val note = noteEdit.text.toString()

            if (amount == null) {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            viewModel.addExpense(
                category = selectedCategory, amount = amount, note = note
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
