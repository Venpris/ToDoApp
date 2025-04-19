package com.example.todoapp

import androidx.room.TypeConverter
import java.time.LocalDate

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): Long? =
        date?.toEpochDay()

    @TypeConverter
    @JvmStatic
    fun toLocalDate(epochDay: Long?): LocalDate? =
        epochDay?.let { LocalDate.ofEpochDay(it) }
}