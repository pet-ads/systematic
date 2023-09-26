package br.all.domain.shared.ddd

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class SearchSourceTest {

    @Test
    fun `valid SEARCH_SOURCE should not throw an exception`() {
        val searchSourceValue = "SearchSource"
        val text = Text(searchSourceValue)
    }

    @Test
    fun `equal SEARCH_SOURCEs should be equal`() {
        val searchSourceValue1 = "SearchSource"
        val searchSourceValue2 = "SearchSource"

        val searchSource1 = Text(searchSourceValue1)
        val searchSource2 = Text(searchSourceValue2)

        assertEquals(searchSource1, searchSource2)
    }

    @Test
    fun `diferent SEARCH_SOURCEs should not be equal`() {
        val searchSourceValue1 = "SearchSource"
        val searchSourceValue2 = "Another SearchSource"

        val searchSource1 = SearchSource(searchSourceValue1)
        val searchSource2 = SearchSource(searchSourceValue2)

        assertNotEquals(searchSource1, searchSource2)
    }

    @Test
    fun `empty SEARCH_SOURCE should throw an exception`() {
        val searchSourceValue = ""
        assertThrows<IllegalArgumentException> {
            Text(searchSourceValue)
        }
    }

    @Test
    fun `blank SEARCH_SOURCE should throw an exception`() {
        val searchSourceValue = "      "
        assertThrows<IllegalArgumentException> {
            Text(searchSourceValue)
        }
    }
}
