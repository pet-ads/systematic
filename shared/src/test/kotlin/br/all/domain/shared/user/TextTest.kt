package br.all.domain.shared.user

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@Tag("UnitTest")
class TextTest {

    @Test
    fun `should create Text when value is valid`() {
        val textValue = "Mussum Ipsum cacilds vidis litro abertis"
        Assertions.assertDoesNotThrow {
            Text(textValue)
        }
    }

    @Test
    fun `should be equal when two Text objects have the same value`() {
        val textValue1 = "Mussum Ipsum cacilds vidis litro abertis Delegadis gente finis"
        val textValue2 = "Mussum Ipsum cacilds vidis litro abertis Delegadis gente finis"

        val text1 = Text(textValue1)
        val text2 = Text(textValue2)

        assertEquals(text1, text2)
    }

    @Test
    fun `should not be equal when two Text objects have different values`() {
        val textValue1 = "Mussum Ipsum cacilds vidis litro abertis"
        val textValue2 = "Per aumento de cachacis eu reclamis"

        val text1 = Text(textValue1)
        val text2 = Text(textValue2)

        assertNotEquals(text1, text2)
    }

    @Test
    fun `should throw exception for empty TEXT`() {
        val textValue = ""
        assertThrows<IllegalArgumentException> {
            Text(textValue)
        }
    }

    @Test
    fun `should throw exception for blank TEXT`() {
        val textValue = "      "
        assertThrows<IllegalArgumentException> {
            Text(textValue)
        }
    }

    @Test
    fun `should throw exception for text starting with a space`() {
        val textValue = " leading space"
        val exception = assertThrows<IllegalArgumentException> {
            Text(textValue)
        }
        assertEquals("The text must not start or end with blank spaces!", exception.message)
    }

    @Test
    fun `should throw exception for text ending with a space`() {
        val textValue = "trailing space "
        val exception = assertThrows<IllegalArgumentException> {
            Text(textValue)
        }
        assertEquals("The text must not start or end with blank spaces!", exception.message)
    }

    @Nested
    @DisplayName("should throw exception for text with invalid characters")
    inner class InvalidTextTest {

        @Test
        fun `like numbers`() {
            val textValue = "Text with 123"
            val exception = assertThrows<IllegalArgumentException> {
                Text(textValue)
            }
            assertEquals("The text must contain only letters and blank spaces!", exception.message)
        }

        @Test
        fun `like @@@`() {
            val textValue = "@@@"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        @DisplayName("like . (dots)")
        fun shouldNotAcceptDotsAsText() {
            val textValue = "."
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        @DisplayName("like , (commas)")
        fun shouldNotAcceptCommasAsText() {
            val textValue = ","
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        fun `like !!!`() {
            val textValue = "!!!"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        fun `like ###`() {
            val textValue = "###"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        fun `like $$$`() {
            val textValue = "$$$"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

//        // This test can cause problems on Windows, but it passes.
//        @Test
//        fun `like %%%`() {
//            val textValue = "%%%"
//            assertThrows<IllegalArgumentException> { Text(textValue) }
//        }

        @Test
        @DisplayName("like : (colons)")
        fun shouldNotAcceptColonsAsText() {
            val textValue = ":"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        @DisplayName("like ; (semicolons)")
        fun shouldNotAcceptSemicolonsAsText() {
            val textValue = ";"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        @DisplayName("like [ ] (brackets)")
        fun shouldNotAcceptBracketsText() {
            val textValue = "[[]]"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }

        @Test
        @DisplayName("like a mix of symbols (#$%!;)")
        fun shouldNotAcceptJustSymbolsText() {
            val textValue = "#$%!;"
            assertThrows<IllegalArgumentException> { Text(textValue) }
        }
    }
}