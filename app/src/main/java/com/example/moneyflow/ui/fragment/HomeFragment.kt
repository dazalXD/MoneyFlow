package com.example.moneyflow.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Data.entity.CategoryEntity
import com.example.moneyflow.Data.entity.ExpenseEntity
import com.example.moneyflow.Data.entity.ExpenseWithCategory
import com.example.moneyflow.MoneyFlowApp
import com.example.moneyflow.R
import com.example.moneyflow.ViewModel.MainViewModel
import com.example.moneyflow.ViewModel.MainViewModelFactory
import com.example.moneyflow.ui.adapeter.ExpenseAdapter
import com.example.moneyflow.utils.DialogHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((requireActivity().application as MoneyFlowApp).repository)
    }

    private lateinit var adapter: ExpenseAdapter
    private lateinit var rvGastos: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var fabAdd: FloatingActionButton

    private var currentCategories: List<CategoryEntity> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGastos = view.findViewById(R.id.rvGastos)
        tvTotal = view.findViewById(R.id.tvTotal)
        fabAdd = view.findViewById(R.id.fabAdd)

        adapter = ExpenseAdapter(
            onLongClick = { item -> showDeleteConfirmation(item) },
            onClick = { item -> showExpenseDetails(item) }
        )

        rvGastos.layoutManager = LinearLayoutManager(requireActivity())
        rvGastos.adapter = adapter

        // Observar categorías
        viewModel.categories.observe(viewLifecycleOwner) { categoriesList ->
            currentCategories = categoriesList
        }

        // Observar cambios en gastos
        viewModel.expenses.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.totalExpenseMes.observe(viewLifecycleOwner) { total ->
            tvTotal.text = "$${String.format("%.2f", total)}"

            val colorRes = when {
                total >= 300.0 -> R.color.error   // Rojo
                total >= 100.0 -> R.color.warning // Amarillo
                else -> R.color.success           // Verde
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun showAddExpenseDialog() {
        if (currentCategories.isEmpty()) {
            Toast.makeText(requireActivity(), "Cargando categorías...", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Agregar Gasto")

        val layout = LinearLayout(requireActivity())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val categorySpinner = Spinner(requireActivity())
        categorySpinner.setPopupBackgroundResource(R.color.white)

        val optionList = currentCategories.map { it.name }.toMutableList()
        optionList.add("+ Crear Nueva Categoría")

        categorySpinner.adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, optionList
        )
        layout.addView(categorySpinner)

        val amountEdit = EditText(requireActivity())
        amountEdit.hint = "Monto"
        amountEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(amountEdit)

        val maxLength = 30
        val noteEdit = EditText(requireActivity())
        noteEdit.hint = "Nota (opcional)"
        noteEdit.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        layout.addView(noteEdit)

        builder.setView(layout)

        builder.setPositiveButton("Agregar") { _, _ ->
            val amountStr = amountEdit.text.toString()
            val amount = amountStr.toDoubleOrNull()
            val note = noteEdit.text.toString()

            if (amount == null) {
                Toast.makeText(requireActivity(), "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val selectedIndex = categorySpinner.selectedItemPosition
            if (selectedIndex == optionList.size - 1) {
                // Seleccionó "+ Crear Nueva Categoría"
                showCreateCategoryDialog { newCategory ->
                    viewModel.addExpense(
                        categoryId = newCategory.id,
                        amount = amount,
                        note = note
                    )
                }
            } else {
                val selectedCategory = currentCategories[selectedIndex]
                viewModel.addExpense(
                    categoryId = selectedCategory.id,
                    amount = amount,
                    note = note
                )
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showCreateCategoryDialog(onCategoryCreated: (CategoryEntity) -> Unit = {}) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Nueva Categoría")

        val layout = LinearLayout(requireActivity())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val nameEdit = EditText(requireActivity())
        nameEdit.hint = "Nombre de la categoría"
        layout.addView(nameEdit)

        val colorSpinner = Spinner(requireActivity())
        colorSpinner.setPopupBackgroundResource(R.color.white)
        val colorOptions = listOf(
            "Verde (#4CAF50)" to "#4CAF50",
            "Azul (#2196F3)" to "#2196F3",
            "Morado (#9C27B0)" to "#9C27B0",
            "Naranja (#FF9800)" to "#FF9800",
            "Rosa (#E91E63)" to "#E91E63",
            "Rojo (#F44336)" to "#F44336",
            "Cian (#00BCD4)" to "#00BCD4",
            "Gris (#607D8B)" to "#607D8B"
        )
        colorSpinner.adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            colorOptions.map { it.first }
        )
        layout.addView(colorSpinner)

        builder.setView(layout)

        builder.setPositiveButton("Crear") { _, _ ->
            val name = nameEdit.text.toString().trim()
            val selectedColorHex = colorOptions[colorSpinner.selectedItemPosition].second

            if (name.isEmpty()) {
                Toast.makeText(requireActivity(), "Escribe un nombre válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            viewModel.addCategory(name, selectedColorHex) { newId ->
                val createdCategory = CategoryEntity(id = newId.toInt(), name = name, colorHex = selectedColorHex, isCustom = true)
                Toast.makeText(requireActivity(), "Categoría '$name' creada", Toast.LENGTH_SHORT).show()
                onCategoryCreated(createdCategory)
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showExpenseDetails(item: ExpenseWithCategory) {
        DialogHelper.showCustomDialog(
            context = requireActivity(),
            title = "Detalles del gasto",
            message = """
                Categoría: ${item.category.name}
                Monto: ${'$'}${item.expense.amount}
                Nota: ${item.expense.note ?: "Sin nota"}
                """.trimIndent(),
            positiveButtonText = "Actualizar",
            negativeButtonText = "Cerrar",
            onAccept = {
                showUpdateExpense(item)
            }
        )
    }

    private fun showDeleteConfirmation(item: ExpenseWithCategory) {
        DialogHelper.showCustomDialog(
            context = requireActivity(),
            title = "Eliminar gasto",
            message = "¿Estás seguro de que deseas eliminar este gasto de $${item.expense.amount} (${item.category.name})?",
            positiveButtonText = "Eliminar",
            negativeButtonText = "Cancelar",
            onAccept = {
                viewModel.deleteExpense(item.expense)
                Toast.makeText(requireActivity(), "Gasto eliminado", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showUpdateExpense(item: ExpenseWithCategory) {
        if (currentCategories.isEmpty()) return

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Actualizar ${item.category.name}")

        val layout = LinearLayout(requireActivity())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val categorySpinner = Spinner(requireActivity())
        categorySpinner.setPopupBackgroundResource(R.color.white)

        val optionList = currentCategories.map { it.name }.toMutableList()
        categorySpinner.adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, optionList
        )
        layout.addView(categorySpinner)

        val amountEdit = EditText(requireActivity())
        amountEdit.hint = "Monto"
        amountEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(amountEdit)

        val maxLength = 30
        val noteEdit = EditText(requireActivity())
        noteEdit.hint = "Nota (opcional)"
        noteEdit.filters = arrayOf(InputFilter.LengthFilter(maxLength))
        layout.addView(noteEdit)

        // Pre-cargar valores actuales
        val currentCatIndex = currentCategories.indexOfFirst { it.id == item.category.id }
        if (currentCatIndex >= 0) {
            categorySpinner.setSelection(currentCatIndex)
        }
        amountEdit.setText(item.expense.amount.toString())
        noteEdit.setText(item.expense.note ?: "")

        builder.setView(layout)

        builder.setPositiveButton("Aceptar cambios") { _, _ ->
            val amountStr = amountEdit.text.toString()
            val amount = amountStr.toDoubleOrNull()
            val note = noteEdit.text.toString()

            if (amount == null) {
                Toast.makeText(requireActivity(), "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val selectedCategory = currentCategories[categorySpinner.selectedItemPosition]

            viewModel.updateExpense(
                ExpenseEntity(
                    id = item.expense.id,
                    categoryId = selectedCategory.id,
                    amount = amount,
                    note = note,
                    date = item.expense.date
                )
            )
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
