package com.ara.aranote.data.local_data_source

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.junit.Test

class DatabaseTypeConvertersTest {

    private val tString = "2021-01-01T00:00"
    private val tDateTime = LocalDateTime.parse(tString)

    private val systemUnderTest = DatabaseTypeConverters()

    @Test
    fun timestampToDate_whenCorrect() {
        // act
        val r = systemUnderTest.stringToLocalDateTime(tString)

        // assert
        assertThat(r).isEqualTo(tDateTime)
    }

    @Test
    fun timestampToDate_whenInCorrect() {
        // act
        val r = systemUnderTest.stringToLocalDateTime("")

        // assert
        assertThat(r).isEqualTo(null)
    }

    @Test
    fun timestampToDate_whenNull() {
        // act
        val r = systemUnderTest.stringToLocalDateTime(null)

        // assert
        assertThat(r).isEqualTo(null)
    }

    @Test
    fun localDateTimeToString_whenCorrect() {
        // act
        val r = systemUnderTest.localDateTimeToString(tDateTime)

        // assert
        assertThat(r).isEqualTo(tString)
    }

    @Test
    fun localDateTimeToString_whenNull() {
        // act
        val r = systemUnderTest.localDateTimeToString(null)

        // assert
        assertThat(r).isEqualTo(null)
    }
}
