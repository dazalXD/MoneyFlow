package com.example.moneyflow.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogHelper {

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

    fun showErrorAlert(context: Context, message: String) {
        showStandardAlert(
            context = context,
            title = "Error",
            message = message,
            negativeButtonText = null
        )
    }
}
