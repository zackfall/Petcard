package com.example.petcard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.petcard.ui.shell.PetCardScaffold
import com.example.petcard.ui.theme.PetcardTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity. Todo el contenido vive en [PetCardScaffold]
 * (TopBar + BottomBar + NavHost). REGLA DE EQUIPO: no añadir pantallas aquí;
 * cada pantalla se conecta en el NavHost del shell.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val pedirAvisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Si lo niega, los recordatorios simplemente no se mostrarán.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pedirPermisoAvisosSiFalta()
        setContent {
            PetcardTheme {
                PetCardScaffold()
            }
        }
    }

    private fun pedirPermisoAvisosSiFalta() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pedirAvisos.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
