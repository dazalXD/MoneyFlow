package com.example.moneyflow.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.moneyflow.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogHelper {

    /**
     * Diálogo estándar usando MaterialAlertDialogBuilder
     */
    fun showStandardAlert(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Aceptar",
        negativeButtonText: String? = "Cancelar",
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClick()
                dialog.dismiss()
            }
            .apply {
                if (negativeButtonText != null) {
                    setNegativeButton(negativeButtonText) { dialog, _ ->
                        onNegativeClick()
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * Muestra el diálogo personalizado usando el layout dialog_custom.xml
     */
    fun showCustomDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Aceptar",
        negativeButtonText: String?,
        onAccept: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        // Vincular vistas del XML custom
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnAccept = view.findViewById<Button>(R.id.btnAccept)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        tvTitle.text = title
        tvMessage.text = message
        btnAccept.text = positiveButtonText
        btnCancel.text = negativeButtonText

        btnAccept.setOnClickListener {
            onAccept()
            alertDialog.dismiss()
        }

        if (negativeButtonText == null) btnCancel.visibility = View.GONE ?: View.VISIBLE
        btnCancel.setOnClickListener {
            onCancel()
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    fun showErrorAlert(context: Context, message: String) {
        showStandardAlert(
            context = context,
            title = "Error",
            message = message,
            negativeButtonText = null
        )
    }
}
