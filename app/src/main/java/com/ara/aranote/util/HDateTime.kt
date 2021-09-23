package com.ara.aranote.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.ocpsoft.prettytime.PrettyTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

val TIME_ZONE = TimeZone.currentSystemDefault()

object HDateTime {
    fun getCurrentDateTime() = Clock.System.now().toLocalDateTime(TIME_ZONE)

    fun getDateTimeFromMillis(value: Long) =
        Instant.fromEpochMilliseconds(value).toLocalDateTime(TIME_ZONE)

    private val prettyTime = PrettyTime()
    fun gerPrettyDateTime(dateTime: LocalDateTime): String =
        prettyTime.format(dateTime.toJavaLocalDateTime())
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

@OptIn(ExperimentalTime::class)
fun LocalDateTime.plus(duration: Duration): LocalDateTime {
    return this.toInstant(TIME_ZONE).plus(duration)
        .toLocalDateTime(TIME_ZONE)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.minus(duration: Duration): LocalDateTime {
    return this.toInstant(TIME_ZONE).minus(duration)
        .toLocalDateTime(TIME_ZONE)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.minus(other: LocalDateTime): Duration {
    return this.toInstant(TIME_ZONE).minus(other.toInstant(TIME_ZONE))
}
