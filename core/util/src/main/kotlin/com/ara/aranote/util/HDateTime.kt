package com.ara.aranote.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.ocpsoft.prettytime.PrettyTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

enum class DateTimeFormatPattern(val pattern: String) {
    DATE("yyyy/MM/dd"),
    TIME("hh:mm a"),
    DATE_TIME("yyyy/MM/dd hh:mm a"),
}

val TIME_ZONE = TimeZone.currentSystemDefault()

object HDateTime {
    fun getCurrentDateTime() = Clock.System.now().toLocalDateTime(TIME_ZONE)

    fun getDateTimeFromMillis(value: Long) =
        Instant.fromEpochMilliseconds(value).toLocalDateTime(TIME_ZONE)

    private val prettyTime = PrettyTime()
    fun gerPrettyDateTime(dateTime: LocalDateTime): String =
        prettyTime.format(dateTime.toJavaLocalDateTime())

    fun formatDateAndTime(dateTime: LocalDateTime, dateTimeFormatPattern: DateTimeFormatPattern): String =
        dateTime.toJavaLocalDateTime().format(
            DateTimeFormatter.ofPattern(dateTimeFormatPattern.pattern)
        )
}

fun LocalDateTime.change(
    year: Int = this.year,
    month: Int = this.monthNumber,
    day: Int = this.dayOfMonth,
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanosecond: Int = this.nanosecond,
) = LocalDateTime(year, month, day, hour, minute, second, nanosecond)

fun LocalDateTime.millis() = toInstant(TIME_ZONE).toEpochMilliseconds()

fun LocalDateTime.plus(duration: Duration): LocalDateTime {
    return this.toInstant(TIME_ZONE).plus(duration)
        .toLocalDateTime(TIME_ZONE)
}

fun LocalDateTime.minus(duration: Duration): LocalDateTime {
    return this.toInstant(TIME_ZONE).minus(duration)
        .toLocalDateTime(TIME_ZONE)
}

fun LocalDateTime.minus(other: LocalDateTime): Duration {
    return this.toInstant(TIME_ZONE).minus(other.toInstant(TIME_ZONE))
}
