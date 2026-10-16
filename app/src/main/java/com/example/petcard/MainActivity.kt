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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.petcard.data.AjustesRepository
import com.example.petcard.ui.shell.PetCardScaffold
import com.example.petcard.ui.theme.PetcardTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Única Activity. Todo el contenido vive en [PetCardScaffold]
 * (TopBar + BottomBar + NavHost). REGLA DE EQUIPO: no añadir pantallas aquí;
 * cada pantalla se conecta en el NavHost del shell.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /** Preferencias de la app (tema); solo puede vivir fuera de los ViewModels. */
    @Inject
    lateinit var ajustes: AjustesRepository

    private val pedirAvisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Si lo niega, los recordatorios simplemente no se mostrarán.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pedirPermisoAvisosSiFalta()
        setContent {
            // null = el usuario nunca eligió tema: sigue al sistema.
            val modoOscuro by ajustes.modoOscuro.collectAsState()
            PetcardTheme(darkTheme = modoOscuro ?: isSystemInDarkTheme()) {
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
