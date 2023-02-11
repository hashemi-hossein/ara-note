package com.ara.aranote.data.localdatasource

import androidx.room.TypeConverter
import com.ara.aranote.util.TAG
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

class DatabaseTypeConverters {
    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? {
        return try {
            value?.let { LocalDateTime.parse(value) }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            println(e)
            null
        }
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}
