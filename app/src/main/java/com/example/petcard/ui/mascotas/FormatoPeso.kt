package com.example.petcard.ui.mascotas

/** Un solo separador decimal, máximo 2 decimales; acepta coma o punto. */
internal fun formatearPeso(valor: String): String {
    // Muchos teclados en español escriben coma. Aquí todo se convierte a punto para tratarlo igual.
    val normalizado = valor.replace(',', '.')

    //filterIndexed recorre cada carácter con su posición y decide si lo conserva. Se queda con un carácter si:
    //
    //es un dígito, o
    //es un punto, pero solo el primero. indexOf('.') devuelve la posición del primer punto; si el punto actual está en esa posición,
    // es el primero y se queda. Los demás se descartan.
    val sinRepetidos = normalizado.filterIndexed { indice, c ->
        c.isDigit() || (c == '.' && normalizado.indexOf('.') == indice)
    }

    // Divide el texto por el punto. Si solo hay una parte (no hay punto), devuelve el número tal cual:
    val partes = sinRepetidos.split('.')
    if (partes.size == 1) return partes[0]

    //drop(1): descarta la parte entera y deja lo que está después del punto.
    //joinToString(""): las junta (por seguridad).
    //take(2): se queda con máximo 2 caracteres.
    val decimales = partes.drop(1).joinToString("").take(2)

    //Si el usuario acaba de escribir el punto ("4."), se conserva el punto, para que pueda seguir escribiendo los decimales.
    return if (decimales.isEmpty()) "${partes[0]}." else "${partes[0]}.$decimales"
}
