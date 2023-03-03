package ara.note.data.localdatasource

import kotlinx.datetime.LocalDateTime
import org.junit.Test
import kotlin.test.assertEquals

class DatabaseTypeConvertersTest {

    private val tString = "2021-01-01T00:00"
    private val tDateTime = LocalDateTime.parse(tString)

    private val systemUnderTest = DatabaseTypeConverters()

    @Test
    fun timestampToDate_whenCorrect() {
        // when
        val r = systemUnderTest.stringToLocalDateTime(tString)

        // then
        assertEquals(r, tDateTime)
    }

    @Test
    fun timestampToDate_whenInCorrect() {
        // when
        val r = systemUnderTest.stringToLocalDateTime("")

        // then
        assertEquals(r, null)
    }

    @Test
    fun timestampToDate_whenNull() {
        // when
        val r = systemUnderTest.stringToLocalDateTime(null)

        // then
        assertEquals(r, null)
    }

    @Test
    fun localDateTimeToString_whenCorrect() {
        // when
        val r = systemUnderTest.localDateTimeToString(tDateTime)

        // then
        assertEquals(r, tString)
    }

    @Test
    fun localDateTimeToString_whenNull() {
        // when
        val r = systemUnderTest.localDateTimeToString(null)

        // then
        assertEquals(r, null)
    }
}
