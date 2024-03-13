package br.all.domain.model.protocol

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

@Tag("UnitTest")
class SearchSourceTest {
    @Nested
    @Tag("ValidClasses")
    @DisplayName("When the given source is valid")
    inner class WhenTheGivenSourceIsValid {
        @Test
        fun `should successfully create a valid search source`() {
            assertDoesNotThrow { SearchSource("Valid SearchSource") }
        }

        @Test
        fun `should a valid string turn into a search source`() {
            assertDoesNotThrow { "Valid SearchSource".toSearchSource() }
        }

        @Test
        fun `should a full lowercase string be transformed to have all of its first letters uppercase`() {
            val source = assertDoesNotThrow { "valid searchsource in lowercase".toSearchSource() }
            assertEquals("Valid Searchsource In Lowercase", source.toString())
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When invalid sources are given")
    inner class WhenInvalidSourcesAreGiven {
        @Test
        fun `should throw if search source contains digits or symbols`() {
            assertThrows<IllegalArgumentException> { SearchSource("H& @ba O-O Dxd5 1m J0ta") }
        }

        @ParameterizedTest(name = "[{index}] source=\"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should throw to any kind of empty search source`(source: String) {
            assertThrows<IllegalArgumentException> { SearchSource(source) }
        }

        @Test
        fun `should throw when trying to convert strings with symbols or digits`() {
            assertThrows<IllegalArgumentException> { "H& @ba O-O Dxd5 1m J0ta".toSearchSource() }
        }

        @ParameterizedTest(name = "[{index}] blankString=\"{0}\"")
        @ValueSource(strings = ["", " ", "   "])
        fun `should throw when trying to get search sources from blank strings`(blankString: String) {
            assertThrows<IllegalArgumentException> { blankString.toSearchSource() }
        }
    }

    @Nested
    @DisplayName("When converting search sources back to string")
    inner class WhenConvertingSearchSourcesBackToString {
        @Test
        @Tag("ValidClasses")
        fun `should a search source turn into a string using its name`() {
            val sourceName = "Valid Search Source"
            val convertedSource = SearchSource(sourceName).toString()
            assertEquals(sourceName, convertedSource)
        }
    }
}
