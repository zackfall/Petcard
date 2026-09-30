package com.example.petcard.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun tipoToString(tipo: TipoEvento): String = tipo.name

    @TypeConverter
    fun stringToTipo(value: String): TipoEvento = TipoEvento.valueOf(value)
}
