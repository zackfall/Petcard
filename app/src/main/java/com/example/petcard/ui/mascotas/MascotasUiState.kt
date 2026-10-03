package com.example.petcard.ui.mascotas

import com.example.petcard.data.local.MascotaEntity

data class MascotasUiState(
    val mascotas: List<MascotaEntity> = emptyList(),
    val cargando: Boolean = true,
)