package com.example.moneyflow.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moneyflow.R
import com.example.moneyflow.utils.DialogHelper
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

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
                    101
                )
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // 1. Obtener NavHostFragment y NavController correctamente
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 2. Configurar la Toolbar con el DrawerLayout y el NavController
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_inicio, R.id.nav_estadistics),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // 3. Vincular el NavigationView con el NavController
        navView.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this) {
            showExitDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
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