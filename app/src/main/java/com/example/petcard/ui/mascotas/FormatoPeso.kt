package com.example.petcard.ui.mascotas

/** Un solo separador decimal, máximo 2 decimales; acepta coma o punto. */
internal fun formatearPeso(valor: String): String {
    val normalizado = valor.replace(',', '.')
    val sinRepetidos = normalizado.filterIndexed { indice, c ->
        c.isDigit() || (c == '.' && normalizado.indexOf('.') == indice)
    }
    val partes = sinRepetidos.split('.')
    if (partes.size == 1) return partes[0]
    val decimales = partes.drop(1).joinToString("").take(2)
    return if (decimales.isEmpty()) "${partes[0]}." else "${partes[0]}.$decimales"
}
