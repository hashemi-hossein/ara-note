package com.ara.aranote.data.localdatasource

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime

class DatabaseTypeConverters {
    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? {
        return try {
            value?.let { LocalDateTime.parse(value) }
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}
