package com.example.moneyflow.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Data.CategoriaGasto
import com.example.moneyflow.Data.entity.ExpenseEntity
import com.example.moneyflow.MoneyFlowApp
import com.example.moneyflow.R
import com.example.moneyflow.ViewModel.MainViewModel
import com.example.moneyflow.ViewModel.MainViewModelFactory
import com.example.moneyflow.ui.adapeter.ExpenseAdapter
import com.example.moneyflow.utils.DialogHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.getValue

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((requireActivity().application as MoneyFlowApp).repository)
    }

    private lateinit var adapter: ExpenseAdapter
    private lateinit var rvGastos: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var fabAdd: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Inicializar las vistas desde el layout inflado
        rvGastos = view.findViewById(R.id.rvGastos)
        tvTotal = view.findViewById(R.id.tvTotal)
        fabAdd = view.findViewById(R.id.fabAdd)

        // RecyclerView con funcionalidad de click largo para eliminar
        adapter = ExpenseAdapter(onLongClick = { expense ->
            showDeleteConfirmation(expense)
        }, onClick = { expense ->
            showExpenseDetails(expense)
        })

        rvGastos.layoutManager = LinearLayoutManager(requireActivity())
        rvGastos.adapter = adapter

        // Observar cambios en gastos
        viewModel.expenses.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { total ->
            tvTotal.text = "$${String.format("%.2f", total)}"


            // 🎨 Lógica de colores según el monto
            val colorRes = when {
                total >= 300.0 -> R.color.error   // Rojo
                total >= 100.0 -> R.color.warning // Amarillo
                else -> R.color.success                // Verde
            }
            tvTotal.setTextColor(ContextCompat.getColor(requireActivity(), colorRes))
        }

        // FAB para agregar gasto
        fabAdd.setOnClickListener {
            showAddExpenseDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun showAddExpenseDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Agregar Gasto")

        val layout = LinearLayout(requireActivity())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val categorySpinner = Spinner(requireActivity())
        categorySpinner.setPopupBackgroundResource(R.color.white)
        val categories = CategoriaGasto.values().map { it.displayName }
        categorySpinner.adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, categories
        )
        layout.addView(categorySpinner)

        val amountEdit = EditText(requireActivity())
        amountEdit.hint = "Monto"
        amountEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(amountEdit)

        val maxLengh = 30
        val noteEdit = EditText(requireActivity())
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
                Toast.makeText(
                    requireActivity(), "Ingresa un monto válido", Toast.LENGTH_SHORT
                ).show()
                return@setPositiveButton
            }

            viewModel.addExpense(
                category = selectedCategory, amount = amount, note = note
            )
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showExpenseDetails(expense: ExpenseEntity) {
        DialogHelper.showCustomDialog(
            context = requireActivity(),
            title = "Detalles del gasto",
            message = """
                Categoría: ${expense.category.displayName}
                Monto: ${'$'}${expense.amount}
                Nota: ${expense.note ?: "Sin nota"}
                """.trimIndent(),
            positiveButtonText = "Actualizar",
            negativeButtonText = "Cerrar",
            onAccept = {
                showUpdateExpense(expense)
            }
        )
    }

    private fun showDeleteConfirmation(expense: ExpenseEntity) {
        DialogHelper.showCustomDialog(
            context = requireActivity(),
            title = "Eliminar gasto",
            message = "¿Estás seguro de que deseas eliminar este gasto de $${expense.amount}?",
            positiveButtonText = "Eliminar",
            negativeButtonText = "Cancelar",
            onAccept = {
                viewModel.deleteExpense(expense)
                Toast.makeText(requireActivity(), "Gasto eliminado", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun showUpdateExpense(expense: ExpenseEntity) {
        val alertdialg = AlertDialog.Builder(requireActivity())
        alertdialg.setTitle("Actualizar ${expense.category.displayName}")

        // no tenemos layout personalizado aún XD después
        val layout = LinearLayout(requireActivity())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val categorySpinner = Spinner(requireActivity())
        categorySpinner.setPopupBackgroundResource(R.color.white)
        val categories = CategoriaGasto.values().map { it.displayName }
        categorySpinner.adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, categories
        )
        layout.addView(categorySpinner)

        val amountEdit = EditText(requireActivity())
        amountEdit.hint = "Monto"
        amountEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(amountEdit)

        val maxLengh = 30
        val noteEdit = EditText(requireActivity())
        noteEdit.hint = "Nota (opcional)"
        noteEdit.filters = arrayOf(InputFilter.LengthFilter(maxLengh))
        layout.addView(noteEdit)

        // Pre-cargar los valores actuales en el formulario
        categorySpinner.setSelection(expense.category.ordinal)
        amountEdit.setText(expense.amount.toString())
        noteEdit.setText(expense.note ?: "")

        alertdialg.setView(layout)

        alertdialg.setPositiveButton("Aceptar cambios") { _, _ ->
            val amountStr = amountEdit.text.toString()
            val amount = amountStr.toDoubleOrNull()
            val selectedCategory = CategoriaGasto.values()[categorySpinner.selectedItemPosition]
            val note = noteEdit.text.toString()

            if (amount == null) {
                Toast.makeText(requireActivity(), "Ingresa un monto válido", Toast.LENGTH_SHORT)
                    .show()
                return@setPositiveButton
            }

            viewModel.updateExpense(
                ExpenseEntity(
                    expense.id,
                    category = selectedCategory,
                    amount = amount,
                    note = note,
                    date = System.currentTimeMillis()
                )
            )
        }

        alertdialg.setNegativeButton("Cancelar", null)
        alertdialg.show()
    }
}