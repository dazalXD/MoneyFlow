package com.example.moneyflow

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyflow.Adapeter.ExpenseAdapter
import com.example.moneyflow.Data.CategoriaGasto
import com.example.moneyflow.Data.entity.ExpenseEntity
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

    val CHANEL_ID = "MoneyFlow"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.enableEdgeToEdge(window)

        // --- BLOQUE PARA PEDIR PERMISOS DE NOTIFICACIÓN ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101 // Código de solicitud
                )
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
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

            // 🎨 Lógica de colores según el monto
            val colorRes = when {
                displayTotal >= 300.0 -> R.color.error   // Rojo
                displayTotal >= 100.0 -> R.color.warning // Amarillo
                else -> R.color.success                // Verde
            }
            tvTotal.setTextColor(ContextCompat.getColor(this, colorRes))
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

    private fun showDeleteConfirmation(expense: ExpenseEntity) {
        DialogHelper.showCustomDialog(
            context = this,
            title = "Eliminar gasto",
            message = "¿Estás seguro de que deseas eliminar este gasto de $${expense.amount}?",
            positiveButtonText = "Eliminar",
            negativeButtonText = "Cancelar",
            onAccept = {
                viewModel.deleteExpense(expense)
                Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun showExpenseDetails(expense: ExpenseEntity) {
        DialogHelper.showCustomDialog(
            context = this, title = "Detalles del gasto", message = """
                Categoría: ${expense.category.displayName}
                Monto: ${'$'}${expense.amount}
                Nota: ${expense.note ?: "Sin nota"}
                """.trimIndent(), positiveButtonText = "Cerrar", negativeButtonText = null
        )
    }

    private fun showAddExpenseDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Gasto")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)

        val categorySpinner = Spinner(this)
        categorySpinner.setPopupBackgroundResource(R.color.white)
        val categories = CategoriaGasto.values().map { it.displayName }
        categorySpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )
        layout.addView(categorySpinner)

        val amountEdit = EditText(this)
        amountEdit.hint = "Monto"
        amountEdit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
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
            lanzarNotificacion()
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

    fun lanzarNotificacion() {

        Log.d("notificaciones", "enviando notificación")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // ceal el canal (obligatorio en android 8.0+)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANEL_ID, "Notificaciones de Gastos", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisa cuando se guarda un gasto"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificacion = NotificationCompat.Builder(this, CHANEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("MoneyFlow")
            .setContentText("hasto guardado con exito")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificacion.build())
    }
}
