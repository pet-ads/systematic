package br.all.domain.model.protocol

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class SearchSourceTest {
    @Test
    fun `Should successfully create a valid search source`() {
        assertDoesNotThrow { SearchSource("Valid SearchSource") }
    }

    @Test
    fun `Should throw if search source contains digits or symbols`() {
        assertThrows<IllegalArgumentException> { SearchSource("H& @ba O-O Dxd5 1m J0ta") }
    }
}
