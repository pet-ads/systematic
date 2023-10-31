package br.all.domain.model.protocol

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class SearchSourceTest {
    @Test
    fun `Should successfully create a valid search source`() {
        assertDoesNotThrow { SearchSource("Valid SearchSource") }
    }
}
